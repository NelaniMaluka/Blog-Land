package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAll(Pageable pageable);

    Page<Post> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Post> findByUserId(Long userId, Pageable pageable);
}
