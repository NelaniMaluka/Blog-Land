package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.mapper.PostBuilder;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.repository.CategoryRepository;
import com.nelani.blog_land_backend.response.PostResponse;
import com.nelani.blog_land_backend.service.CategoryService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    @Cacheable(value = "categoryPosts", key = "#categoryId + '_' + #page + '_' + #size + '_' + #order")
    public Page<PostResponse> getByCategoryId(UUID categoryId, int page, int size, String order) {
        String setOrder = (order == null || !order.equals("oldest")) ? "latest" : "oldest";

        // Checks if the category exists
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found."));

        // Determine sort direction
        Sort.Direction direction = setOrder.equals("latest") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "createdAt"));

        // Fetch only posts in the given category
        Page<Post> postPage = categoryRepository.findByCategoryId(categoryId, pageable);

        // Convert to PostResponse while retaining pagination metadata
        return postPage.map(PostBuilder::generatePost);
    }
}
