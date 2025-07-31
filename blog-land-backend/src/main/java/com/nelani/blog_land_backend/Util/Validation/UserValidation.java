package com.nelani.blog_land_backend.Util.Validation;

import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.model.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class UserValidation {

    public static Optional<User> getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            return Optional.empty();
        }
        return Optional.of((User) auth.getPrincipal());
    }

    public static User getOrThrowUnauthorized() {
        return UserValidation.getAuthenticatedUser()
                .orElseThrow(() -> new BadCredentialsException("No authenticated user found"));
    }

    public static void assertUserExists(Optional<User> user, String message){
        if (user.isEmpty()){
            throw new IllegalArgumentException(message);
        }
    }

    public static void assertUserDoesNotExist(Optional<User> user, String message) {
        if (user.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void assertUserProvider(User user, Provider provider, String message){
        if (!user.getProvider().equals(provider)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void assertUserIsLocal(User user, String message){
        if (!user.getProvider().equals(Provider.LOCAL)) {
            throw new IllegalArgumentException();
        }
    }

    public static void assertUserEmailsMatch(User user, String email, String message) {
        if (!user.getEmail().equals(email)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void assertUserPasswordsMatch(User user, PasswordEncoder passwordEncoder, String password, String message){
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void assertNewAndOldPasswordsMatch(User user, PasswordEncoder passwordEncoder, String newPassword){
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("You cannot reuse your current password. Please choose a new password.");
        }
    }

    public static void assertNewAndRepeatPasswordsMatch(String newPassword,String repeatPassword){
        if (!newPassword.equals(repeatPassword)) {
            throw new IllegalArgumentException("Repeat password does not match the new password.");
        }
    }

}
