package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.dto.CategoryWithPostsDTO;
import com.nelani.blog_land_backend.model.Category;
import com.nelani.blog_land_backend.repository.CustomPostRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CustomPostRepositoryImpl implements CustomPostRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<CategoryWithPostsDTO> findTopCategoriesWithPosts() {
        // Step 1: Get top 6 category IDs with at least 5 posts
        List<Long> topCategoryIds = entityManager.createQuery(
                        "SELECT p.category.id FROM Post p GROUP BY p.category.id " +
                                "HAVING COUNT(p.id) >= 5 ORDER BY COUNT(p.id) DESC", Long.class)
                .setMaxResults(6)
                .getResultList();

        if (topCategoryIds.isEmpty()) return Collections.emptyList();

        // Step 2: Fetch categories and their posts
        List<Category> categories = entityManager.createQuery(
                        "SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.posts p " +
                                "WHERE c.id IN :ids", Category.class)
                .setParameter("ids", topCategoryIds)
                .getResultList();

        // Step 3: Map to DTOs
        return categories.stream()
                .map(c -> new CategoryWithPostsDTO(c.getId(), c.getName(), c.getPosts()))
                .collect(Collectors.toList());
    }
}

