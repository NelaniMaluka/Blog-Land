package com.nelani.blog_land_backend.Util.Validation;

import com.nelani.blog_land_backend.model.PasswordResetToken;

import java.time.LocalDateTime;
import java.util.Optional;

public class PasswordTokenValidation {

    public static void assertTokenExists(PasswordResetToken token){
        if (token == null){
            throw new IllegalArgumentException("Your password reset link is invalid. Please request a new one.");
        }
    }

    public static void assertTokenIsActive(Optional<PasswordResetToken> tokenOpt){
        if (tokenOpt.isPresent() && !tokenOpt.get().isUsed()) {
            throw new IllegalArgumentException("A password reset link has already been sent. Please check your email or wait for the link to expire.");
        }
    }

    public static void assertTokenIsUsed(PasswordResetToken token){
        if (token.isUsed()){
            throw new IllegalArgumentException("Your password reset link has already been used. Please request a new one");
        }
    }

    public static void assertTokenNotExpired(PasswordResetToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Your password reset link has expired. Please request a new one.");
        }
    }

}
