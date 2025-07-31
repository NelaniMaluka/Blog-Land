package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.Validation.FormValidation;
import com.nelani.blog_land_backend.Util.Validation.UserValidation;
import com.nelani.blog_land_backend.dto.PasswordDto;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.service.ChangePasswordService;

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
    public void changePasswordWithOldPassword(PasswordDto passwordDto) {
        // Validate fields
        String oldPassword = FormValidation.assertValidatedPassword(passwordDto.getOldPassword(), "Old Password");
        String newPassword = FormValidation.assertValidatedPassword(passwordDto.getNewPassword(), "New Password");
        String repeatPassword = FormValidation.assertValidatedPassword(passwordDto.getRepeatPassword(), "Repeat Password");

        // Checks if repeat password and new password match
        UserValidation.assertNewAndRepeatPasswordsMatch(newPassword, repeatPassword);

        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Checks if user is Local
        UserValidation.assertUserIsLocal(user, "OAuth user's can not change their password.");

        // Checks if provided old password and users password match
        UserValidation.assertUserPasswordsMatch(user, passwordEncoder, oldPassword, "The current password provided does not match your existing password.");

        // Checks if user password and new password don't match
        UserValidation.assertNewAndOldPasswordsMatch(user, passwordEncoder, newPassword);

        // Update current password and encodes it
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user); // Save the user with the new password
    }
}
