package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.Validation.PasswordTokenValidation;
import com.nelani.blog_land_backend.Util.Validation.UserValidation;
import com.nelani.blog_land_backend.dto.EmailDto;
import com.nelani.blog_land_backend.dto.ForgotPasswordDto;
import com.nelani.blog_land_backend.model.PasswordResetToken;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.PasswordResetTokenRepository;
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
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserValidation userValidation;
    private final PasswordTokenValidation passwordTokenValidation;

    public ForgotPasswordServiceImpl(PasswordEncoder passwordEncoder, EmailService emailService, UserRepository userRepository, PasswordResetTokenRepository passwordResetTokenRepository, UserValidation userValidation, PasswordTokenValidation passwordTokenValidation) {
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userValidation = userValidation;
        this.passwordTokenValidation = passwordTokenValidation;
    }

    @Override
    @Transactional
    public void requestPasswordReset(EmailDto emailDto){
        // Check if the user exists
        User user = userValidation.assertUserExists(null, emailDto.getEmail());

        // Checks if user is Local
        userValidation.assertUserIsLocal(user,"OAuth user's can not change their password.");

        // Checks if there is an active token

        // Checks if there is an active reset token
        passwordTokenValidation.assertTokenIsActive(user);

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
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(hashedToken)
                .user(user)
                .expiryDate(expiryDate)
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();

        passwordResetTokenRepository.save(resetToken); // save the password reset object

        emailService.sendPasswordResetEmail(user.getEmail(), token); // Generate redirect email
    };

    @Override
    @Transactional
    public void changePassword(ForgotPasswordDto passwordDto){
        // Checks if repeat password and new password match
        userValidation.assertPasswordsMatch(passwordDto.getNewPassword(), passwordDto.getRepeatPassword());

        // Checks it the token is valid
        String hashedToken = DigestUtils.sha256Hex(passwordDto.getToken());
        Optional<PasswordResetToken> tokenEntity = passwordResetTokenRepository.findByToken(hashedToken);
        passwordTokenValidation.assertTokenExists(tokenEntity.get());

        // Checks if the token hasn't expired
        passwordTokenValidation.assertTokenNotExpired(tokenEntity.get());

        // Checks if the token hasn't been used
        passwordTokenValidation.assertTokenIsUsed(tokenEntity.get());

        // Get the user from the token
        User user = tokenEntity.get().getUser();

        // Checks if user password and new password don't match
        userValidation.assertNewAndOldPasswordsDoNotMatch(user, passwordDto.getNewPassword());

        // Update the users password
        user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));

        userRepository.save(user); // Save the user with the new password

        // Update the forgot password entity
        PasswordResetToken passwordResetToken = tokenEntity.get();
        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);
    };
}
