package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.dto.ForgotPasswordDto;
import com.nelani.blog_land_backend.model.PasswordReset;
import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.notifications.EmailService;
import com.nelani.blog_land_backend.repository.PasswordResetRepository;
import com.nelani.blog_land_backend.util.validation.UserValidation;
import com.nelani.blog_land_backend.dto.PasswordDto;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.service.PasswordResetService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;

@Log4j2
@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserValidation userValidation;
    private final PasswordResetRepository passwordResetRepository;
    private final EmailService emailService;

    public PasswordResetServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository,
            UserValidation userValidation, PasswordResetRepository passwordResetRepository, EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userValidation = userValidation;
        this.passwordResetRepository = passwordResetRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        // Check if the user exists
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No account is associated with that email."));

        log.info("Password reset requested for user: {}", email);

        // Check if the user is local
        if (!user.getProvider().equals(Provider.LOCAL)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "OAuth users cannot change their password.");
        }

        // Check if there is an active reset token
        LocalDateTime now = LocalDateTime.now();
        passwordResetRepository
                .findByUserAndUsedFalseAndExpiryDateAfter(user, now)
                .ifPresent(token -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "A password reset link has already been sent. " +
                                    "Please check your email or wait for the link to expire.");
                });

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

        passwordResetRepository.save(resetToken);

        emailService.sendPasswordResetEmail(user.getEmail(), token);

        log.info("Password reset email sent to user: {}", email);
    }

    @Override
    @Transactional
    public void changePassword(ForgotPasswordDto passwordDto, String token) {
        // Checks if repeat password and new password match
        if (!passwordDto.newPassword().equals(passwordDto.repeatPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Repeat password does not match the new password.");
        }

        // Checks it the token is valid
        String hashedToken = DigestUtils.sha256Hex(token);

        // Fetch token entity or throw exception
        var tokenEntity = passwordResetRepository.findByToken(hashedToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Your password reset link is invalid. Please request a new one."));

        log.info("Password reset requested verification for user: {}", tokenEntity.getUser().getEmail());

        // Checks if the token hasn't expired
        if (tokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Your password reset link has expired. Please request a new one.");
        }

        // Checks if the token hasn't been used
        if (tokenEntity.isUsed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Your password reset link has already been used. Please request a new one.");
        }

        // Get the user from the token
        User user = tokenEntity.getUser();

        // Checks if user password and new password don't match
        if (passwordEncoder.matches(passwordDto.newPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You cannot reuse your current password. Please choose a new password.");
        }

        // Update the users password
        user.setPassword(passwordEncoder.encode(passwordDto.newPassword()));

        userRepository.save(user); // Save the user with the new password

        // Update the forgot password entity
        tokenEntity.setUsed(true);
        passwordResetRepository.save(tokenEntity);

        log.info("Successfully changed password for user: {}", user.getEmail());
    };

    @Override
    @Transactional
    public void changePasswordWithOldPassword(PasswordDto passwordDto) {
        log.info("Password change requested for user.");

        // Checks if repeat password and new password match
        if (!passwordDto.newPassword().equals(passwordDto.repeatPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Repeat password does not match the new password.");
        }

        // Get current authenticated user
        User user = userValidation.getAuthenticatedUser();

        // Checks if user is Local
        if (!user.getProvider().equals(Provider.LOCAL)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "OAuth users cannot change their password.");
        }

        // Checks if provided old password and users password match
        if (!passwordEncoder.matches(passwordDto.oldPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The current password provided does not match your existing password.");
        }

        // Checks if user password and new password don't match
        if (passwordEncoder.matches(passwordDto.newPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You cannot reuse your current password. Please choose a new password.");
        }

        // Update current password and encode it
        user.setPassword(passwordEncoder.encode(passwordDto.newPassword()));
        userRepository.save(user); // Save the user with the new password

        log.info("Password changed successfully for user with email: {}", user.getEmail());
    }

}
