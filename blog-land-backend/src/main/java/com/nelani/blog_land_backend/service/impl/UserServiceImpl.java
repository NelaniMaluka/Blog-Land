package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.ResponseBuilder;
import com.nelani.blog_land_backend.Util.UserValidation;
import com.nelani.blog_land_backend.response.UserResponse;
import com.nelani.blog_land_backend.Util.FormValidation;
import com.nelani.blog_land_backend.config.JwtUtil;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ResponseEntity<?> updateUserDetails(User updateUser) {
        // trim and validate fields
        String firstname = FormValidation.trimAndValidate(updateUser.getFirstname(), "Firstname");
        String lastname = FormValidation.trimAndValidate(updateUser.getLastname(), "Lastname");
        String email = FormValidation.validatedEmail(updateUser.getEmail());
        String provider = FormValidation.trimAndValidate(updateUser.getProvider(), " Provider");
        String profileIconUrl = updateUser.getProfileIconUrl();
        String location = updateUser.getLocation();

        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

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

        userRepository.save(user);

        // Generate new token with updated email
        String newToken = jwtUtil.generateJwtToken(user);

        UserResponse responsePayload = new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                user.getProvider(),
                user.getProfileIconUrl(),
                user.getLocation(),
                newToken);

        return ResponseEntity.ok(responsePayload);
    }

}