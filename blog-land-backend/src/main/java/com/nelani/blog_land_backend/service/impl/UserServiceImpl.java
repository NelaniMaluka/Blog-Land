package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.Builders.UserBuilder;
import com.nelani.blog_land_backend.Util.Caches.UserCacheHelper;
import com.nelani.blog_land_backend.Util.Sockets.UserSocket;
import com.nelani.blog_land_backend.Util.Validation.FileValidation;
import com.nelani.blog_land_backend.Util.Validation.ModerationValidator;
import com.nelani.blog_land_backend.Util.Validation.UserValidation;
import com.nelani.blog_land_backend.dto.UserDto;
import com.nelani.blog_land_backend.model.ExperienceLevel;
import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.repository.CommentRepository;
import com.nelani.blog_land_backend.repository.LikeRepository;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.response.UserResponse;
import com.nelani.blog_land_backend.Util.Validation.FormValidation;
import com.nelani.blog_land_backend.Util.JwtUtil;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.response.UserSummaryAnalyticsResponse;
import com.nelani.blog_land_backend.service.UserService;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final ModerationValidator moderationValidator;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserSocket userSocket;
    private final JwtUtil jwtUtil;
    private final UserCacheHelper userCacheHelper;

    private static final String UPLOAD_DIR = "ProfileIcons/";
    private static final String BackendBaseUrl = "https://blog-land.onrender.com/";

    public UserServiceImpl(ModerationValidator moderationValidator, UserRepository userRepository,
            PostRepository postRepository, CommentRepository commentRepository, LikeRepository likeRepository,
            UserSocket userSocket, JwtUtil jwtUtil, UserCacheHelper userCacheHelper) {
        this.moderationValidator = moderationValidator;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.userSocket = userSocket;
        this.jwtUtil = jwtUtil;
        this.userCacheHelper = userCacheHelper;
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

        userCacheHelper.evictAllForUser(user.getId(), user.getNaniId()); // Evict the user data

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

        userCacheHelper.evictAllForUser(user.getId(), user.getNaniId()); // Evict the user data

        return "Successfully uploaded icon: " + user.getProfileIconUrl();
    }

    @Override
    @Transactional
    @Cacheable(value = "user", key = "T(com.nelani.blog_land_backend.Util.Validation.UserValidation).getCurrentUserId()")
    public UserResponse getUserDetails() {
        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();
        return UserBuilder.buildLoggedInUser(user); // send formatted user data
    }

    @Override
    @Transactional
    @Cacheable(value = "publicUser", key = "#nanoId", unless = "#result == null")
    public UserResponse getPublicUserDetails(String nanoId) {
        FormValidation.assertRequiredField(nanoId, "User Id");

        // Get user data user
        Optional<User> user = userRepository.findByNaniId(nanoId);
        UserValidation.assertUserExists(user, "User does not exist.");

        return UserBuilder.publicUserWithMinimalDetails(user.get()); // send formatted user data
    }

    @Override
    @Transactional
    public String updateUserDetails(UserDto updateUser) {
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

        if (socials != null) {
            user.getSocials().clear(); // Hibernate tracks changes
            socials.entrySet().stream()
                    .filter(e -> e.getKey() != null && !e.getKey().isBlank()
                            && e.getValue() != null && !e.getValue().isBlank())
                    .forEach(e -> user.getSocials().put(e.getKey(), e.getValue()));
        }

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

        // Update the socket
        userSocket.updateUser(user);
        userSocket.updatePublicUser(user);

        userCacheHelper.evictAllForUser(user.getId(), user.getNaniId()); // Evict the user data

        return jwtUtil.generateJwtToken(user); // Generate new token with updated email
    }

    @Override
    @Transactional
    public void deleteUserDetails() {
        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();
        userRepository.delete(user); // Deletes user

        userCacheHelper.evictAllForUser(user.getId(), user.getNaniId()); // Evict the user data
    }

    @Override
    @Transactional
    public UserSummaryAnalyticsResponse getUserSummaryAnalytics() {
        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        long postCount = postRepository.countByUser(user);
        long viewCount = postRepository.getTotalViewsByUserId(user.getId());
        long commentCount = commentRepository.countCommentsOnUserPosts(user.getId());
        long likeCount = likeRepository.countLikesOnUserPosts(user.getId());

        return UserSummaryAnalyticsResponse.builder()
                .totalPosts(postCount)
                .totalViews(viewCount)
                .totalComments(commentCount)
                .totalLikes(likeCount)
                .build();
    };

}