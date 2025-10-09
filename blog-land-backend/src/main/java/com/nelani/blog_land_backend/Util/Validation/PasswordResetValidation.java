package com.nelani.blog_land_backend.util.validation;

import com.nelani.blog_land_backend.model.PasswordReset;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.PasswordResetRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Log4j2
public class PasswordResetValidation {

    private final PasswordResetRepository tokenRepository;

    public PasswordResetValidation(PasswordResetRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public void assertTokenExists(PasswordReset token) {
        if (token == null) {
            log.warn("Password reset token validation failed: token not found or invalid.");
            throw new IllegalArgumentException("Your password reset link is invalid. Please request a new one.");
        }
    }

    public void assertTokenIsActive(User user) {
        LocalDateTime now = LocalDateTime.now();
        tokenRepository.findByUserAndUsedFalseAndExpiryDateAfter(user, now)
                .orElseThrow(() -> {
                    log.debug("Active password reset token not found for user ID {}. " +
                            "A reset link may have already been sent or expired.", user.getId());
                    return new IllegalArgumentException("A password reset link has already been sent. " +
                            "Please check your email or wait for the link to expire.");
                });
    }

    public void assertTokenIsUsed(PasswordReset token) {
        if (token.isUsed()) {
            log.warn("Attempted reuse of a password reset token for user ID {}.", token.getUser().getId());
            throw new IllegalArgumentException("Your password reset link has already been used. Please request a new one.");
        }
    }

    public void assertTokenNotExpired(PasswordReset token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.debug("Expired password reset token detected for user ID {}.", token.getUser().getId());
            throw new IllegalArgumentException("Your password reset link has expired. Please request a new one.");
        }
    }
}
