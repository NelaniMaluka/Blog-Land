package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.Builders.UserBuilder;
import com.nelani.blog_land_backend.Util.Validation.FileValidation;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final ModerationValidator moderationValidator;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private static final String UPLOAD_DIR = "ProfileIcons/";
    private static final String BackendBaseUrl = "https://blog-land.onrender.com/";

    public UserServiceImpl(ModerationValidator moderationValidator, UserRepository userRepository, JwtUtil jwtUtil) {
        this.moderationValidator = moderationValidator;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public String removeUserProfileImage() {
        // Fetch the user from the repository
        User currentUser = UserValidation.getOrThrowUnauthorized();
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Remove the file locally
        String fileName = FileValidation.removeFile(user.getProfileIconUrl().replace(BackendBaseUrl, ""));

        // Update URL
        user.setProfileIconUrl("");
        userRepository.saveAndFlush(user);

        return fileName;
    }

    @Override
    @Transactional
    public String saveUserProfileImage(MultipartFile file) {
        // Save file with validation class
        String fileName = FileValidation.saveFile(UPLOAD_DIR, file);

        // Build file URL
        String fileUrl = BackendBaseUrl + UPLOAD_DIR + fileName;

        // Fetch the user from the repository
        User currentUser = UserValidation.getOrThrowUnauthorized();
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update URL
        user.setProfileIconUrl(fileUrl);
        userRepository.saveAndFlush(user);

        return "Successfully uploaded icon: " + user.getProfileIconUrl();
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