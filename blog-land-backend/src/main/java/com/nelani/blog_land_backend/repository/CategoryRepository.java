package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.Category;
import com.nelani.blog_land_backend.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByName(String name);

    // Category filter, only published
    @Query("SELECT p FROM Post p WHERE p.category.id = :categoryId AND " +
            "p.isDraft = false AND (p.scheduledAt IS NULL OR p.scheduledAt <= CURRENT_TIMESTAMP)")
    Page<Post> findByCategoryId(@Param("categoryId") UUID categoryId, Pageable pageable);
}

