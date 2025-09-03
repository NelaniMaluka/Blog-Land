package com.nelani.blog_land_backend.Util.Validation;

import com.nelani.blog_land_backend.model.Like;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.LikeRepository;

import java.util.Optional;

public class LikeValidation {

    public static void assertLikeExists(Optional<Like> like) {
        if (like.isEmpty()) {
            throw new IllegalArgumentException("Like does not exist.");
        }
    }

    public static void assertLikeDoesNotExists(LikeRepository likeRepository, User user, Post post){
        if (likeRepository.findByUserAndPost(user, post).isPresent()) {
            throw new IllegalArgumentException("User has already liked this post");
        };
    }

    public static void assertLikeBelongsToUser(Like like, User user) {
        if (!like.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "This like does not belong to the authenticated user.");
        }
    }

}
