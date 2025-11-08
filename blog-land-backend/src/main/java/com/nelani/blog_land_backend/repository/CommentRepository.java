package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.Comment;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    long countByPost(Post post);

    Page<Comment> findByPostId(UUID postId, Pageable pageable);

    List<Comment> findByUserIdAndPostId(UUID userId, UUID PostId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.user = :user OR c.post IN (SELECT p FROM Post p WHERE p.user = :user)")
    int deleteByUser(@Param("user") User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.post = :post")
    int deleteByPost(@Param("post") Post post);
}
