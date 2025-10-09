package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.util.builders.UserBuilder;
import com.nelani.blog_land_backend.util.caches.UserCacheHelper;
import com.nelani.blog_land_backend.dto.UpdateUserDto;
import com.nelani.blog_land_backend.sockets.UserSocket;
import com.nelani.blog_land_backend.util.validation.FileValidation;
import com.nelani.blog_land_backend.util.validation.ModerationValidator;
import com.nelani.blog_land_backend.util.validation.UserValidation;
import com.nelani.blog_land_backend.repository.CommentRepository;
import com.nelani.blog_land_backend.repository.LikeRepository;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.response.UserResponse;
import com.nelani.blog_land_backend.security.JwtUtil;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.response.UserSummaryAnalyticsResponse;
import com.nelani.blog_land_backend.service.UserService;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final UserValidation userValidation;

    private static final String UPLOAD_DIR = "userProfileIcons/";
    private static final String BackendBaseUrl = "https://blog-land.onrender.com/";

    public UserServiceImpl(ModerationValidator moderationValidator, UserRepository userRepository,
            PostRepository postRepository, CommentRepository commentRepository, LikeRepository likeRepository,
            UserSocket userSocket, JwtUtil jwtUtil, UserCacheHelper userCacheHelper, UserValidation userValidation) {
        this.moderationValidator = moderationValidator;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.userSocket = userSocket;
        this.jwtUtil = jwtUtil;
        this.userCacheHelper = userCacheHelper;
        this.userValidation = userValidation;
    }

    @Override
    @Transactional
    public String removeUserProfileImage() {
        // Fetch the user from the repository
        User currentUser = UserValidation.getAuthenticatedUser();
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
        User currentUser = UserValidation.getAuthenticatedUser();
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
    @Cacheable(value = "user", key = "T(com.nelani.blog_land_backend.util.validation.UserValidation).getCurrentUserId()")
    public UserResponse getUserDetails() {
        // Get current authenticated user
        User user = UserValidation.getAuthenticatedUser();
        return UserBuilder.buildLoggedInUser(user); // send formatted user data
    }

    @Override
    @Transactional
    @Cacheable(value = "publicUser", key = "#nanoId", unless = "#result == null")
    public UserResponse getPublicUserDetails(String nanoId) {
        // Get user data user
        User user = userValidation.assertUserExists(nanoId, null);

        return UserBuilder.publicUserWithMinimalDetails(user); // send formatted user data
    }

    @Override
    @Transactional
    public String updateUserDetails(UpdateUserDto updateUser) {
        // Get current authenticated user
        User user = UserValidation.getAuthenticatedUser();

        // Checks if the emails match
        userValidation.assertUserEmailsMatch(user, updateUser.email());

        // Validates user provider
        userValidation.assertUserProvider(user, updateUser.provider());

        if (updateUser.socials() != null) {
            user.getSocials().clear(); // Hibernate tracks changes
            updateUser.socials().entrySet().stream()
                    .filter(e -> e.getKey() != null && !e.getKey().isBlank()
                            && e.getValue() != null && !e.getValue().isBlank())
                    .forEach(e -> user.getSocials().put(e.getKey(), e.getValue()));
        }

        user.setFirstname(updateUser.firstname());
        user.setLastname(updateUser.lastname());
        user.setLocation(updateUser.location());
        user.setExperience(updateUser.experience());
        user.setSocials(updateUser.socials());
        user.setSummary(updateUser.summary());
        user.setTitle(updateUser.title());

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
        User user = UserValidation.getAuthenticatedUser();
        userRepository.delete(user); // Deletes user

        userCacheHelper.evictAllForUser(user.getId(), user.getNaniId()); // Evict the user data
    }

    @Override
    @Transactional
    public UserSummaryAnalyticsResponse getUserSummaryAnalytics() {
        // Get current authenticated user
        User user = UserValidation.getAuthenticatedUser();

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