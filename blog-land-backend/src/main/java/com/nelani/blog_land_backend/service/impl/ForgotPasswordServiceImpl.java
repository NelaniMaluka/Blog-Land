package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.util.validation.PasswordResetValidation;
import com.nelani.blog_land_backend.util.validation.UserValidation;
import com.nelani.blog_land_backend.dto.EmailDto;
import com.nelani.blog_land_backend.dto.ForgotPasswordDto;
import com.nelani.blog_land_backend.model.PasswordReset;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.PasswordResetRepository;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.notifications.EmailService;
import com.nelani.blog_land_backend.service.ForgotPasswordService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.codec.digest.DigestUtils;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final UserValidation userValidation;
    private final PasswordResetValidation passwordResetValidation;

    public ForgotPasswordServiceImpl(PasswordEncoder passwordEncoder, EmailService emailService,
            UserRepository userRepository, PasswordResetRepository passwordResetRepository,
            UserValidation userValidation, PasswordResetValidation passwordResetValidation) {
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.userValidation = userValidation;
        this.passwordResetValidation = passwordResetValidation;
    }

    @Override
    @Transactional
    public void requestPasswordReset(EmailDto emailDto) {
        // Check if the user exists
        User user = userValidation.assertUserExists(null, emailDto.email());

        // Checks if user is Local
        userValidation.assertUserIsLocal(user, "OAuth user's can not change their password.");

        // Checks if there is an active reset token
        passwordResetValidation.assertTokenIsActive(user);

        // Generate request token
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String token = Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
                .signWith(key)
                .compact();

        String hashedToken = DigestUtils.sha256Hex(token);

        // Generate password reset object
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15);
        PasswordReset resetToken = PasswordReset.builder()
                .token(hashedToken)
                .user(user)
                .expiryDate(expiryDate)
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();

        passwordResetRepository.save(resetToken); // save the password reset object

        emailService.sendPasswordResetEmail(user.getEmail(), token); // Generate redirect email
    };

    @Override
    @Transactional
    public void changePassword(ForgotPasswordDto passwordDto) {
        // Checks if repeat password and new password match
        userValidation.assertPasswordsMatch(passwordDto.newPassword(), passwordDto.repeatPassword());

        // Checks it the token is valid
        String hashedToken = DigestUtils.sha256Hex(passwordDto.token());
        Optional<PasswordReset> tokenEntity = passwordResetRepository.findByToken(hashedToken);
        passwordResetValidation.assertTokenExists(tokenEntity.get());

        // Checks if the token hasn't expired
        passwordResetValidation.assertTokenNotExpired(tokenEntity.get());

        // Checks if the token hasn't been used
        passwordResetValidation.assertTokenIsUsed(tokenEntity.get());

        // Get the user from the token
        User user = tokenEntity.get().getUser();

        // Checks if user password and new password don't match
        userValidation.assertNewAndOldPasswordsDoNotMatch(user, passwordDto.newPassword());

        // Update the users password
        user.setPassword(passwordEncoder.encode(passwordDto.newPassword()));

        userRepository.save(user); // Save the user with the new password

        // Update the forgot password entity
        PasswordReset passwordReset = tokenEntity.get();
        passwordReset.setUsed(true);
        passwordResetRepository.save(passwordReset);
    };
}
