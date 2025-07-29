package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {
    long count();

    @Query(value = "SELECT * FROM posts ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Post findRandomPost();

    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Post> findByCategoryIdOrderByCreatedAtDesc(Long categoryId, Pageable pageable);

    Page<Post> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);


}
