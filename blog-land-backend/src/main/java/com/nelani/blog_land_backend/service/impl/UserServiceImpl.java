package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.ResponseBuilder;
import com.nelani.blog_land_backend.Util.UserBuilder;
import com.nelani.blog_land_backend.Util.UserValidation;
import com.nelani.blog_land_backend.model.ExperienceLevel;
import com.nelani.blog_land_backend.response.UserResponse;
import com.nelani.blog_land_backend.Util.FormValidation;
import com.nelani.blog_land_backend.config.JwtUtil;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public ResponseEntity<?> getUserDetails() {
        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        UserResponse responsePayload = UserBuilder.buildLoggedInUser(user);

        return ResponseEntity.ok(responsePayload);
    }

    @Override
    @Transactional
    public ResponseEntity<?> updateUserDetails(User updateUser) {
        // trim and validate fields
        Long id = FormValidation.trimAndValidate(updateUser.getId(), "User Id");
        String firstname = FormValidation.trimAndValidate(updateUser.getFirstname(), "Firstname");
        String lastname = FormValidation.trimAndValidate(updateUser.getLastname(), "Lastname");
        String email = FormValidation.validatedEmail(updateUser.getEmail());
        String provider = FormValidation.trimAndValidate(updateUser.getProvider(), " Provider");
        String profileIconUrl = updateUser.getProfileIconUrl();
        String location = updateUser.getLocation();
        ExperienceLevel experienceLevel = updateUser.getExperience();
        Map<String, String> socials = updateUser.getSocials();

        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Checks if the user id matches the auth user id
        if (!user.getId().equals(id)) {
            return ResponseBuilder.unauthorized("Unauthorized",
                    "The provided user Id does not match the authenticated user id");
        }

        // Restricts email change to local users
        if (!user.getProvider().equals("LOCAL") && !user.getEmail().equals(email)) {
            return ResponseBuilder.unauthorized("Authentication Type Error",
                    "OAuth user's can not change their email.");
        }

        // Validates user provider
        if (!user.getProvider().equals(provider)) {
            return ResponseBuilder.unauthorized("Authentication Type Error",
                    "The provided authentication type is not valid.");
        }

        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setEmail(email);
        user.setProfileIconUrl(profileIconUrl);
        user.setLocation(location);
        user.setExperience(experienceLevel);
        user.setSocials(socials);

        userRepository.save(user);

        // Generate new token with updated email
        String newToken = jwtUtil.generateJwtToken(user);

        return ResponseEntity.ok(newToken);
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteUserDetails(Long id) {
        Long Id = FormValidation.trimAndValidate(id, "User Id");

        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Checks if the user id matches the auth user id
        if (!user.getId().equals(id)) {
            return ResponseBuilder.unauthorized("Unauthorized",
                    "The provided user Id does not match the authenticated user id"+user.getId() + " " + Id);
        }

        // Deletes user
        userRepository.delete(user);

        return ResponseEntity.ok("Success, Successfully deleted your account");
    }
}