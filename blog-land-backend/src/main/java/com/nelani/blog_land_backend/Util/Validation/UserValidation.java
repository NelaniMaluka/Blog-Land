package com.nelani.blog_land_backend.Util.Validation;

import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;
import jakarta.validation.ValidationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserValidation {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserValidation(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            throw new BadCredentialsException("No authenticated user found.");
        }
        return (User) auth.getPrincipal();
    }

    public User assertUserExists(String nanoId, String email) {
        if (nanoId != null) return userRepository.findByNaniId(nanoId).orElseThrow(() -> new ValidationException("User does not exist."));
        if (email != null) return userRepository.findByEmail(email).orElseThrow(() -> new ValidationException("No account is associated with that email."));
        throw new ValidationException("User does not exist.");
    }

    public void assertUserDoesNotExist(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new ValidationException("User already exists with the provided email.");
        }
    }

    public void assertUserProvider(User user, Provider provider) {
        if (!user.getProvider().equals(provider)) {
            throw new ValidationException("This account was registered with " + user.getProvider()
                    + " . Please log in using that provider.");
        }
    }

    public void assertUserIsLocal(User user, String message) {
        if (!user.getProvider().equals(Provider.LOCAL)) {
            throw new ValidationException(message);
        }
    }

    public void assertUserEmailsMatch(User user, String email) {
        if (!user.getEmail().equals(email)) {
            throw new IllegalArgumentException("Provided email does not match the user's registered email.");
        }
    }

    public void assertPasswordsMatch(String password, String repeatPassword) {
        if (!password.equals(repeatPassword)) {
            throw new IllegalArgumentException("Repeat password does not match the new password.");
        }
    }

    public void assertEncodedPasswordsMatch(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new IllegalArgumentException("The current password provided does not match your existing password.");
        }
    }

    public void assertNewAndOldPasswordsDoNotMatch(User user, String newPassword) {
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("You cannot reuse your current password. Please choose a new password.");
        }
    }

    public static Long getCurrentUserId() {
        User user = UserValidation.getAuthenticatedUser();
        return user.getId();
    }

}
