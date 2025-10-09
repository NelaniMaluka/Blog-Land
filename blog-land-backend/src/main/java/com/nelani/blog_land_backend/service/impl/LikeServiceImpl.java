package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.util.caches.LikeCacheHelper;
import com.nelani.blog_land_backend.sockets.LikesSocket;
import com.nelani.blog_land_backend.util.validation.LikeValidation;
import com.nelani.blog_land_backend.util.validation.PostValidation;
import com.nelani.blog_land_backend.util.validation.UserValidation;
import com.nelani.blog_land_backend.model.Like;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.LikeRepository;
import com.nelani.blog_land_backend.response.LikeResponse;
import com.nelani.blog_land_backend.service.LikeService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final LikesSocket likesSocket;
    private final LikeCacheHelper likeCacheHelper;
    private final PostValidation postValidation;
    private final LikeValidation likeValidation;

    public LikeServiceImpl(LikeRepository likeRepository, LikesSocket likesSocket, LikeCacheHelper likeCacheHelper,
            PostValidation postValidation, LikeValidation likeValidation) {
        this.likeRepository = likeRepository;
        this.likesSocket = likesSocket;
        this.likeCacheHelper = likeCacheHelper;
        this.postValidation = postValidation;
        this.likeValidation = likeValidation;
    }

    @Override
    @Transactional
    @Cacheable(value = "postLikesCount", key = "#postId")
    public long getPostLikesCount(long postId) {
        // Checks if the post exists
        Post existingPost = postValidation.assertPostExist(postId);

        return likeRepository.countByPost(existingPost);
    }

    @Override
    @Transactional
    @Cacheable(value = "userLikes", key = "T(com.nelani.blog_land_backend.util.validation.UserValidation).getCurrentUserId()")
    public List<LikeResponse> getUserLikes() {
        // Get current authenticated user
        User user = UserValidation.getAuthenticatedUser();

        // get user likes
        java.util.List<Like> userLikes = likeRepository.findAllByUser(user);

        // Format the response
        return userLikes.stream()
                .map(like -> com.nelani.blog_land_backend.response.LikeResponse.builder()
                        .likeId(like.getId())
                        .likedAt(like.getLikedAt())
                        .postId(like.getPost().getId())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public String addLike(long postId) {
        // Checks if the post exists
        Post existingPost = postValidation.assertPostExist(postId);

        // Get current authenticated user
        User user = UserValidation.getAuthenticatedUser();

        // Check if the like already exists
        likeValidation.assertLikeDoesNotExists(user, existingPost);

        Like like = Like.builder()
                .post(existingPost)
                .user(user)
                .likedAt(LocalDateTime.now())
                .build();

        likeRepository.save(like);

        // update socket
        likesSocket.updatePostLikes(likeRepository, existingPost);

        likeCacheHelper.evictAllForPost(user.getId(), postId); // Evict Likes

        return "Like Successfully saved";
    }

    @Override
    @Transactional
    public String removeLike(long likeId) {
        // Check if like exists
        Like like = likeValidation.assertLikeExists(likeId);

        // Checks if the post exists
        Post post = postValidation.assertPostExist(like.getPost().getId());

        // Get current authenticated user
        User user = UserValidation.getAuthenticatedUser();

        // Check if the like belongs to the user
        likeValidation.assertLikeBelongsToUser(like, user);

        likeRepository.delete(like);

        // update socket
        likesSocket.updatePostLikes(likeRepository, post);

        likeCacheHelper.evictAllForPost(user.getId(), post.getId()); // Evict Likes

        return "Like Successfully removed";
    }
}
