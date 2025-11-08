package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.model.UserSocial;
import com.nelani.blog_land_backend.repository.*;
import com.nelani.blog_land_backend.response.LoginResponse;
import com.nelani.blog_land_backend.security.JwtService;
import com.nelani.blog_land_backend.mapper.UserMapper;
import com.nelani.blog_land_backend.cache.UserCacheHelper;
import com.nelani.blog_land_backend.dto.UpdateUserDto;
import com.nelani.blog_land_backend.sockets.UserSocket;
import com.nelani.blog_land_backend.util.validation.FileValidation;
import com.nelani.blog_land_backend.util.validation.ModerationValidator;
import com.nelani.blog_land_backend.util.validation.UserValidation;
import com.nelani.blog_land_backend.response.UserResponse;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.service.UserService;

import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@Log4j2
@Service
public class UserServiceImpl implements UserService {

    private final ModerationValidator moderationValidator;
    private final UserRepository userRepository;
    private final UserSocialRepository userSocialRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final UserSocket userSocket;
    private final JwtService jwtService;
    private final UserCacheHelper userCacheHelper;
    private final UserValidation userValidation;

    private static final String UPLOAD_DIR = "userProfileIcons/";
    private static final String BackendBaseUrl = "https://blog-land.onrender.com/";

    public UserServiceImpl(ModerationValidator moderationValidator, UserRepository userRepository,
            UserSocialRepository userSocialRepository, PostRepository postRepository,
            CommentRepository commentRepository, LikeRepository likeRepository,
            PasswordResetRepository passwordResetRepository, UserSocket userSocket, JwtService jwtService,
            UserCacheHelper userCacheHelper, UserValidation userValidation) {
        this.moderationValidator = moderationValidator;
        this.userRepository = userRepository;
        this.userSocialRepository = userSocialRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.userSocket = userSocket;
        this.jwtService = jwtService;
        this.userCacheHelper = userCacheHelper;
        this.userValidation = userValidation;
    }

    @Override
    @Transactional
    public void saveUserProfileImage(MultipartFile file) {
        log.info("Starting profile image upload for user");

        // Save file with validation class
        String fileName = FileValidation.saveFile(UPLOAD_DIR, file);

        // Build file URL
        String fileUrl = BackendBaseUrl + UPLOAD_DIR + fileName;

        // Fetch the user from the repository
        User currentUser = userValidation.getAuthenticatedUser();
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Update URL
        user.setProfileIconUrl(fileUrl);
        userRepository.saveAndFlush(user);

        // Evict user cache
        userCacheHelper.evictAllForUser(user.getEmail(), user.getNaniId());

        log.info("Profile image upload completed for userId: {}", user.getId());
    }

    @Override
    @Transactional
    public void removeUserProfileImage() {
        log.info("Starting profile image removal for user");

        // Fetch the user
        User currentUser = userValidation.getAuthenticatedUser();
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Remove the file locally
        String filePath = user.getProfileIconUrl().replace(BackendBaseUrl, "");
        FileValidation.removeFile(filePath);

        // Update URL
        user.setProfileIconUrl("");
        userRepository.saveAndFlush(user);

        // Evict user cache
        userCacheHelper.evictAllForUser(user.getEmail(), user.getNaniId());

        log.info("Profile image removal completed for userId: {}", user.getId());
    }

    @Override
    @Transactional
    @Cacheable(value = "user", key = "T(org.springframework.security.core.context.SecurityContextHolder).context.authentication.name")
    public UserResponse getUserDetails() {
        // Get current authenticated user
        User user = userValidation.getAuthenticatedUser();

        return UserMapper.buildLoggedInUser(user, Collections.emptyList());
    }

    @Override
    @Transactional
    @Cacheable(value = "publicUser", key = "#nanoId", unless = "#result == null")
    public UserResponse getPublicUserDetails(String nanoId) {
        // Get user data user
        User user = userRepository.findByNaniId(nanoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist."));

        return UserMapper.publicUserWithMinimalDetails(user); // send formatted user data
    }

    @Override
    @Transactional
    public LoginResponse updateUserDetails(UpdateUserDto updateUser) {
        // Get current authenticated user
        User user = userValidation.getAuthenticatedUser();
        log.info("Starting updateUserDetails for userId: {} and email: {}", user.getId(), user.getEmail());

        // Checks if the emails match
        if (!user.getEmail().equals(updateUser.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The provided email does not match the registered email for this account.");
        }

        // Validates user provider
        if (!user.getProvider().equals(updateUser.provider())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "This account was registered with " + user.getProvider()
                            + ". Please log in using that provider.");
        }

        // Update basic fields
        user.setFirstname(updateUser.firstname());
        user.setLastname(updateUser.lastname());
        user.setLocation(updateUser.location());
        user.setExperience(updateUser.experience());
        user.setSummary(updateUser.summary());
        user.setTitle(updateUser.title());

        List<UserSocial> updatedSocials = List.of();

        // Update socials if provided
        if (updateUser.socials() != null && !updateUser.socials().isEmpty()) {
            moderationValidator.userModeration(user, updateUser.socials());

            // Delete existing socials
            var existingSocials = userSocialRepository.findByUser(user);
            userSocialRepository.deleteAll(existingSocials);

            // Map DTO to new UserSocial entities and save
            updatedSocials = updateUser.socials().entrySet().stream()
                    .filter(e -> e.getKey() != null && !e.getKey().isBlank()
                            && e.getValue() != null && !e.getValue().isBlank())
                    .map(e -> UserSocial.builder()
                            .user(user)
                            .platform(e.getKey())
                            .url(e.getValue())
                            .build())
                    .toList();
            userSocialRepository.saveAll(updatedSocials);
        }

        // Save user details
        userRepository.save(user);

        // Update sockets and evict cache
        userSocket.updateUser(user, updatedSocials);
        userSocket.updatePublicUser(user);
        userCacheHelper.evictAllForUser(user.getEmail(), user.getNaniId());

        log.info("Finished updateUserDetails for userId: {}", user.getId());

        return LoginResponse.builder()
                .token(jwtService.generateToken(user))
                .expiresIn(86400000)
                .user(UserMapper.buildLoggedInUser(user, updatedSocials))
                .build();
    }

    @Override
    @Transactional
    public void deleteUserDetails() {
        // Get current authenticated user
        User user = userValidation.getAuthenticatedUser();
        log.info("Deleting user with ID: {} and email: {}", user.getId(), user.getEmail());

        // Delete user
        deleteUserAndRelations(user);

        // Evict user cache
        userCacheHelper.evictAllForUser(user.getEmail(), user.getNaniId());

        log.info("User deleted successfully: ID {}", user.getId());
    }

    @Transactional
    public void deleteUserAndRelations(User existingUser) {
        likeRepository.deleteByUser(existingUser);
        commentRepository.deleteByUser(existingUser);
        postRepository.deleteByUser(existingUser);
        passwordResetRepository.deleteByUser(existingUser);
        userSocialRepository.deleteByUser(existingUser);
        userRepository.delete(existingUser);
    }

}