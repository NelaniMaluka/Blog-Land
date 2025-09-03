package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.Validation.LikeValidation;
import com.nelani.blog_land_backend.Util.Validation.PostValidation;
import com.nelani.blog_land_backend.Util.Validation.UserValidation;
import com.nelani.blog_land_backend.model.Like;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.LikeRepository;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.response.LikeResponse;
import com.nelani.blog_land_backend.service.LikeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LikeServiceImpl implements LikeService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;

    public LikeServiceImpl(PostRepository postRepository, LikeRepository likeRepository) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
    }

    @Override
    @Transactional
    public long getPostLikesCount(long postId) {
        // Checks if the post exists
        Optional<Post> post = postRepository.findById(postId);
        Post existingPost = PostValidation.assertPostExist(post);

        return likeRepository.countByPost(existingPost);
    }

    @Override
    @Transactional
    public List<LikeResponse> getUserLikes() {
        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

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
        Optional<Post> post = postRepository.findById(postId);
        Post existingPost = PostValidation.assertPostExist(post);

        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Check if the like already exists
        LikeValidation.assertLikeDoesNotExists(likeRepository, user, existingPost);

        Like like = Like.builder()
                .post(existingPost)
                .user(user)
                .likedAt(LocalDateTime.now())
                .build();

        likeRepository.save(like);

        return "Like Successfully saved";
    }

    @Override
    @Transactional
    public String removeLike(long likeId) {
        // Check if like exists
        Optional<Like> like = likeRepository.findById(likeId);
        LikeValidation.assertLikeExists(like);

        // Checks if the post exists
        Optional<Post> post = postRepository.findById(like.get().getPost().getId());
        PostValidation.assertPostExists(post);

        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Check if the like belongs to the user
        LikeValidation.assertLikeBelongsToUser(like.get(), user);

        likeRepository.delete(like.get());

        return "Like Successfully removed";
    }
}
