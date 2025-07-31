package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.Validation.FormValidation;
import com.nelani.blog_land_backend.Util.Validation.PasswordTokenValidation;
import com.nelani.blog_land_backend.Util.Validation.UserValidation;
import com.nelani.blog_land_backend.model.PasswordResetToken;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.PasswordResetTokenRepository;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.service.EmailService;
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
import java.util.Map;
import java.util.Optional;

@Service
public class ForgotPasswordRequestImpl implements ForgotPasswordService {

    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public ForgotPasswordRequestImpl(PasswordEncoder passwordEncoder, EmailService emailService, UserRepository userRepository, PasswordResetTokenRepository passwordResetTokenRepository, UserRepository userRepository1) {
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Override
    @Transactional
    public void requestPasswordReset(Map<String, String> payload){
        // Validate fields
        String email = FormValidation.assertValidatedEmail(payload.get("email"));

        // Check if the user exists
        Optional<User> optionalUser = userRepository.findByEmail(email);
        UserValidation.assertUserExists(optionalUser, "No account is associated with that email.");

        // Checks if user is Local
        UserValidation.assertUserIsLocal(optionalUser.get(),"OAuth user's can not change their password.");

        // Checks if there is an active token
        LocalDateTime now = LocalDateTime.now();
        Optional<PasswordResetToken> activeToken = passwordResetTokenRepository
                .findByUserAndUsedFalseAndExpiryDateAfter(optionalUser.get(), now);

        // Checks if there is an active reset token
        PasswordTokenValidation.assertTokenIsActive(activeToken.get());

        // Generate request token
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String token = Jwts.builder()
                .setSubject(optionalUser.get().getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
                .signWith(key)
                .compact();

        String hashedToken = DigestUtils.sha256Hex(token);

        // Generate password reset object
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15);
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(hashedToken)
                .user(optionalUser.get())
                .expiryDate(expiryDate)
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();

        passwordResetTokenRepository.save(resetToken); // save the password reset object

        emailService.sendPasswordResetEmail(optionalUser.get().getEmail(), token); // Generate redirect email
    };

    @Override
    @Transactional
    public void changePassword(Map<String, String> payload){
        // Validate fields
        String token = FormValidation.assertRequiredField(payload.get("token"), "Token");
        String newPassword = FormValidation.assertValidatedPassword(payload.get("newPassword"), "New Password");
        String repeatPassword = FormValidation.assertValidatedPassword(payload.get("repeatPassword"), "Repeat Password");

        // Checks if repeat password and new password match
        UserValidation.assertNewAndRepeatPasswordsMatch(newPassword, repeatPassword);

        // Checks it the token is valid
        String hashedToken = DigestUtils.sha256Hex(token);
        Optional<PasswordResetToken> tokenEntity = passwordResetTokenRepository.findByToken(hashedToken);
        PasswordTokenValidation.assertTokenExists(tokenEntity.get());

        // Checks if the token hasn't expired
        PasswordTokenValidation.assertTokenExpired(tokenEntity.get());

        // Checks if the token hasn't been used
        PasswordTokenValidation.assertTokenIsUsed(tokenEntity.get());

        // Get the user from the token
        User user = tokenEntity.get().getUser();

        // Checks if user password and new password don't match
        UserValidation.assertNewAndOldPasswordsMatch(user, passwordEncoder, newPassword);

        // Update the users password
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user); // Save the user with the new password

        // Update the forgot password entity
        PasswordResetToken passwordResetToken = tokenEntity.get();
        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);
    };
}
