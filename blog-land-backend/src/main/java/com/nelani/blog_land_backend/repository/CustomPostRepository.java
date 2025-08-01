package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.dto.CategoryWithPostsDTO;

import java.util.List;

public interface CustomPostRepository {
    List<CategoryWithPostsDTO> findTopCategoriesWithPosts();
}

