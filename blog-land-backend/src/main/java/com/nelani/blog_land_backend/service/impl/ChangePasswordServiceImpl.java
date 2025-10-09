package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.util.validation.UserValidation;
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
    private final UserValidation userValidation;

    public ChangePasswordServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository,
            UserValidation userValidation) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userValidation = userValidation;
    }

    @Override
    public void changePasswordWithOldPassword(PasswordDto passwordDto) {
        // Checks if repeat password and new password match
        userValidation.assertPasswordsMatch(passwordDto.newPassword(), passwordDto.repeatPassword());

        // Get current authenticated user
        User user = UserValidation.getAuthenticatedUser();

        // Checks if user is Local
        userValidation.assertUserIsLocal(user, "OAuth user's can not change their password.");

        // Checks if provided old password and users password match
        userValidation.assertEncodedPasswordsMatch(user.getPassword(), passwordDto.oldPassword());

        // Checks if user password and new password don't match
        userValidation.assertNewAndOldPasswordsDoNotMatch(user, passwordDto.newPassword());

        // Update current password and encodes it
        user.setPassword(passwordEncoder.encode(passwordDto.newPassword()));

        userRepository.save(user); // Save the user with the new password
    }
}
