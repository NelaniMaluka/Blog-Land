package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAll(Pageable pageable);

    Page<Comment> findByPostId(Long postId, Pageable pageable);

    Page<Comment> findByUserId(Long userId, Pageable pageable);
}
