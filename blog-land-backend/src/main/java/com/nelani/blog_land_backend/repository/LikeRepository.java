package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.Like;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {

    // Count likes for a specific post
    long countByPost(Post post);

    // Get all likes by a user
    List<Like> findAllByUser(User user);

    // Check if a user liked a specific post
    Optional<Like> findByUserAndPost(User user, Post post);

    @Modifying
    @Transactional
    @Query("DELETE FROM Like l WHERE l.user = :user OR l.post IN (SELECT p FROM Post p WHERE p.user = :user)")
    int deleteByUser(@Param("user") User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM Like l WHERE l.post = :post")
    int deleteByPost(@Param("post") Post post);
}
