package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.Category;
import com.nelani.blog_land_backend.model.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
    long count();

    long countByCategory(Category category);

    @Query(value = "SELECT * FROM posts ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Post findRandomPost();

    @Query("SELECT COUNT(p) FROM Post p WHERE p.category.id = :categoryId")
    int countByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Post p JOIN FETCH p.category c WHERE " +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Post> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<Post> findAllByOrderByViewCountDesc(Pageable pageable);

    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Post> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Post> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

}
