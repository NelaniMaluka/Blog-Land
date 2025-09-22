package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.Like;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    // Count likes for a specific post
    long countByPost(Post post);

    // Get all likes by a user
    List<Like> findAllByUser(User user);

    // Optional: check if a user liked a specific post
    Optional<Like> findByUserAndPost(User user, Post post);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.user.id = :userId")
    Long countLikesOnUserPosts(@Param("userId") Long userId);
}
