package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.Builders.UserBuilder;
import com.nelani.blog_land_backend.Util.Validation.ModerationValidator;
import com.nelani.blog_land_backend.Util.Validation.UserValidation;
import com.nelani.blog_land_backend.model.ExperienceLevel;
import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.response.UserResponse;
import com.nelani.blog_land_backend.Util.Validation.FormValidation;
import com.nelani.blog_land_backend.Util.JwtUtil;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.service.UserService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final ModerationValidator moderationValidator;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(ModerationValidator moderationValidator, UserRepository userRepository, JwtUtil jwtUtil) {
        this.moderationValidator = moderationValidator;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public UserResponse getUserDetails() {
        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();
        return UserBuilder.buildLoggedInUser(user); // send formatted user data
    }

    @Override
    @Transactional
    public String updateUserDetails(User updateUser) {
        // Validate fields
        String firstname = FormValidation.assertRequiredField(updateUser.getFirstname(), "Firstname");
        String lastname = FormValidation.assertRequiredField(updateUser.getLastname(), "Lastname");
        String email = FormValidation.assertValidatedEmail(updateUser.getEmail());
        Provider provider = FormValidation.assertRequiredField(updateUser.getProvider(), " Provider");
        String profileIconUrl = updateUser.getProfileIconUrl();
        String location = updateUser.getLocation();
        ExperienceLevel experienceLevel = updateUser.getExperience();
        Map<String, String> socials = updateUser.getSocials();
        String summary = updateUser.getSummary();
        String title = updateUser.getTitle();

        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Checks if the emails match
        UserValidation.assertUserEmailsMatch(user, email, "Provided email does not match the user's registered email.");

        // Restricts email change to local users
        UserValidation.assertUserIsNotLocal(user, email, "OAuth user's can not change their email.");

        // Validates user provider
        UserValidation.assertUserProvider(user, provider, "This account was registered with " + user.getProvider()
                + " . Please log in using your " + user.getProvider() + " provider.");

        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setProfileIconUrl(profileIconUrl);
        user.setLocation(location);
        user.setExperience(experienceLevel);
        user.setSocials(socials);
        user.setSummary(summary);
        user.setTitle(title);

        // Moderate content
        moderationValidator.userModeration(user);

        userRepository.save(user); // save the user

        return jwtUtil.generateJwtToken(user); // Generate new token with updated email
    }

    @Override
    @Transactional
    public void deleteUserDetails() {
        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();
        userRepository.delete(user); // Deletes user
    }

}