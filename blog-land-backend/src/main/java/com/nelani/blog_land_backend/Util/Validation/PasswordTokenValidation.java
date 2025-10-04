package com.nelani.blog_land_backend.Util.Validation;

import com.nelani.blog_land_backend.model.PasswordResetToken;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.PasswordResetTokenRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PasswordTokenValidation {
    private final PasswordResetTokenRepository tokenRepository;

    public PasswordTokenValidation(PasswordResetTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public void assertTokenExists(PasswordResetToken token){
        if (token == null){
            throw new IllegalArgumentException("Your password reset link is invalid. Please request a new one.");
        }
    }

    public void assertTokenIsActive(User user){
        LocalDateTime now = LocalDateTime.now();
        tokenRepository
                .findByUserAndUsedFalseAndExpiryDateAfter(user, now)
                .orElseThrow(() -> new IllegalArgumentException("A password reset link has already been sent. Please check your email or wait for the link to expire."));
    }

    public void assertTokenIsUsed(PasswordResetToken token){
        if (token.isUsed()){
            throw new IllegalArgumentException("Your password reset link has already been used. Please request a new one");
        }
    }

    public void assertTokenNotExpired(PasswordResetToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Your password reset link has expired. Please request a new one.");
        }
    }

}
