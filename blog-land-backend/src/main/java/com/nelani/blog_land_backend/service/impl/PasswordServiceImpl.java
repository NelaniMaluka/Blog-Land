package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.FormValidation;
import com.nelani.blog_land_backend.Util.UserValidation;
import com.nelani.blog_land_backend.dto.PasswordDto;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.response.ErrorResponse;
import com.nelani.blog_land_backend.service.PasswordService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PasswordServiceImpl implements PasswordService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public PasswordServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<?> changePasswordWithOldPassword(PasswordDto passwordDto) {
        try {
            // trim and validate fields
            String oldPassword = FormValidation.trimAndValidate(passwordDto.getOldPassword(), "Old Password");
            String newPassword = FormValidation.trimAndValidate(passwordDto.getNewPassword(), "New Password");
            String repeatPassword = FormValidation.trimAndValidate(passwordDto.getRepeatPassword(), "Repeat Password");

            // Validate password format
            if (!FormValidation.isValidPassword(oldPassword)) {
                return ResponseEntity
                        .badRequest()
                        .body(new ErrorResponse("Validation Error",
                                "Old password must contain at least 8 characters, an uppercase letter, a digit, and a special character."));
            }

            if (!FormValidation.isValidPassword(newPassword)) {
                return ResponseEntity
                        .badRequest()
                        .body(new ErrorResponse("Validation Error",
                                "New password must contain at least 8 characters, an uppercase letter, a digit, and a special character."));
            }

            if (!FormValidation.isValidPassword(repeatPassword)) {
                return ResponseEntity
                        .badRequest()
                        .body(new ErrorResponse("Validation Error",
                                "Repeat password must contain at least 8 characters, an uppercase letter, a digit, and a special character."));
            }

            // Checks if repeat password and new password match
            if (!newPassword.equals(repeatPassword)) {
                return ResponseEntity
                        .badRequest()
                        .body(new ErrorResponse("Validation Error",
                                "Repeat password does not match the new password."));
            }

            // Get the authenticated user
            Optional<User> optionalUser = UserValidation.getAuthenticatedUser();
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("error", "No authenticated user found"));
            }

            User user = optionalUser.get();

            // Checks if provided old password and users password match
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                return ResponseEntity
                        .badRequest()
                        .body(new ErrorResponse("Validation Error",
                                "The current password provided does not match your existing password."));
            }

            // Checks if user password and new password don't match
            if (passwordEncoder.matches(newPassword, user.getPassword())) {
                return ResponseEntity
                        .badRequest()
                        .body(new ErrorResponse("Validation Error",
                                "You cannot reuse your current password. Please choose a new password."));
            }

            // Update current password and encodes it
            user.setPassword(passwordEncoder.encode(newPassword));

            // Save the user with the new password
            userRepository.save(user);

            return ResponseEntity.ok("Success, Your password was changed successfully! You're all set.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal Server Error",
                            "An unexpected error occurred while saving your message. Please try again later."));
        }
    }
}
