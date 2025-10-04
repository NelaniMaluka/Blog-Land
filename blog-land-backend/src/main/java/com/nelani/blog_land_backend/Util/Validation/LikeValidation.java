package com.nelani.blog_land_backend.Util.Validation;

import com.nelani.blog_land_backend.model.Like;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.LikeRepository;
import org.springframework.stereotype.Component;

@Component
public class LikeValidation {
    private final LikeRepository likeRepository;

    public LikeValidation(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public Like assertLikeExists(long likeId) {
        return likeRepository.findById(likeId)
                .orElseThrow(() -> new IllegalArgumentException("Like does not exist."));
    }

    public void assertLikeDoesNotExists(User user, Post post) {
        if (likeRepository.findByUserAndPost(user, post).isPresent()) {
            throw new IllegalArgumentException("User has already liked this post");
        }
    }

    public void assertLikeBelongsToUser(Like like, User user) {
        if (!like.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "This like does not belong to the authenticated user.");
        }
    }

}
