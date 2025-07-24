package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.FormValidation;
import com.nelani.blog_land_backend.Util.ResponseBuilder;
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

import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> requestPasswordReset(Map<String, String> payload){
        // Validate the email
        String email = FormValidation.validatedEmail(payload.get("email"));

        // Check if the user exists
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()){
            return ResponseBuilder.invalid("User Not Found",
                    "No account is associated with that email.");
        }

        // Checks if user is Local
        if (!optionalUser.get().getProvider().equals("LOCAL")){
            return ResponseBuilder.unauthorized("Authentication Type Error",
                    "OAuth user's can not change their password.");
        }

        // Checks if there is an active token
        LocalDateTime now = LocalDateTime.now();
        Optional<PasswordResetToken> activeToken = passwordResetTokenRepository
                .findByUserAndUsedFalseAndExpiryDateAfter(optionalUser.get(), now);

        if (activeToken.isPresent()) {
            return ResponseBuilder.invalid("Token Already Issued",
                    "A password reset link has already been sent. Please check your email or wait for the link to expire.");
        }

        // Generate request token
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        String token = Jwts.builder()
                .setSubject(optionalUser.get().getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
                .signWith(key)
                .compact();

        String hashedToken = DigestUtils.sha256Hex(token);

        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15);
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(hashedToken)
                .user(optionalUser.get())
                .expiryDate(expiryDate)
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();

        passwordResetTokenRepository.save(resetToken);

        // Generate redirect email
        emailService.sendPasswordResetEmail(optionalUser.get().getEmail(), token);

        return ResponseEntity.ok("Success, Password reset link sent to your email.");
    };

    @Override
    @Transactional
    public ResponseEntity<?> changePassword(Map<String, String> payload){

        // Trim and validate fields
        String token = FormValidation.trimAndValidate(payload.get("token"), "Token");
        String newPassword = FormValidation.validatedPassword(payload.get("newPassword"), "New Password");
        String repeatPassword = FormValidation.validatedPassword(payload.get("repeatPassword"), "Repeat Password");

        // Checks if repeat password and new password match
        if (!newPassword.equals(repeatPassword)) {
            return ResponseBuilder.invalid("Validation Error",
                    "Repeat password does not match the new password.");
        }

        // Checks it the token is valid
        String hashedToken = DigestUtils.sha256Hex(token);
        Optional<PasswordResetToken> tokenEntity = passwordResetTokenRepository.findByToken(hashedToken);
        if (tokenEntity.isEmpty()){
            return ResponseBuilder.invalid("Invalid Token",
                    "Your password reset link is invalid. Please request a new one.");
        }

        // Checks if the token hasn't expired
        if (tokenEntity.get().getExpiryDate().equals(LocalDateTime.now())){
            return ResponseBuilder.unauthorized("Expired Token",
                    "Your password reset link is expired. Please request a new one.");
        }

        // Checks if the token hasn't been used
        if (tokenEntity.get().isUsed()){
            return ResponseBuilder.unauthorized("Invalid Token",
                    "Your password reset link has already been used. Please request a new one");
        }

        User user = tokenEntity.get().getUser();

        // Checks if user password and new password don't match
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            return ResponseBuilder.invalid("Validation Error",
                    "You cannot reuse your current password. Please choose a new password.");
        }

        // Update the users password
        user.setPassword(passwordEncoder.encode(newPassword));

        // Save the user with the new password
        userRepository.save(user);

        // Update the forgot password entity
        PasswordResetToken passwordResetToken = tokenEntity.get();
        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);

        return ResponseEntity.ok("Success, Your password was changed successfully! You're all set.");
    };
}
