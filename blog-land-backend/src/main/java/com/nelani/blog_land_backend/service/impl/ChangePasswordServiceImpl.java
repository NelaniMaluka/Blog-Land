package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.FormValidation;
import com.nelani.blog_land_backend.Util.ResponseBuilder;
import com.nelani.blog_land_backend.Util.UserValidation;
import com.nelani.blog_land_backend.dto.PasswordDto;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.service.ChangePasswordService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class ChangePasswordServiceImpl implements ChangePasswordService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public ChangePasswordServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<?> changePasswordWithOldPassword(PasswordDto passwordDto) {
            // trim and validate fields
            String oldPassword = FormValidation.validatedPassword(passwordDto.getOldPassword(), "Old Password");
            String newPassword = FormValidation.validatedPassword(passwordDto.getNewPassword(), "New Password");
            String repeatPassword = FormValidation.validatedPassword(passwordDto.getRepeatPassword(), "Repeat Password");

            // Checks if repeat password and new password match
            if (!newPassword.equals(repeatPassword)) {
                return ResponseBuilder.invalid("Validation Error",
                        "Repeat password does not match the new password.");
            }

            // Get current authenticated user
            User user = UserValidation.getOrThrowUnauthorized();

            // Checks if provided old password and users password match
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                return ResponseBuilder.invalid("Validation Error",
                        "The current password provided does not match your existing password.");
            }

            // Checks if user password and new password don't match
            if (passwordEncoder.matches(newPassword, user.getPassword())) {
                return ResponseBuilder.invalid("Validation Error",
                        "You cannot reuse your current password. Please choose a new password.");
            }

            // Update current password and encodes it
            user.setPassword(passwordEncoder.encode(newPassword));

            // Save the user with the new password
            userRepository.save(user);

            return ResponseEntity.ok("Success, Your password was changed successfully! You're all set.");
    }
}
