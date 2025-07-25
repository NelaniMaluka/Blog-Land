package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.FormValidation;
import com.nelani.blog_land_backend.Util.PostBuilder;
import com.nelani.blog_land_backend.Util.ResponseBuilder;
import com.nelani.blog_land_backend.Util.UserValidation;
import com.nelani.blog_land_backend.model.Category;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.CategoryRepository;
import com.nelani.blog_land_backend.response.PostResponse;
import com.nelani.blog_land_backend.service.PostService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final CategoryRepository categoryRepository;

    public PostServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<?> getByCategoryId(Long categoryId) {
        Long id = FormValidation.trimAndValidate(categoryId, "Category Id");

        // Checks if the category exists
        if (categoryRepository.findById(id).isEmpty()) {
            return ResponseBuilder.invalid("Validation Error",
                    "Category with ID " + id + " does not exist.");
        }
        Category category = categoryRepository.findById(id).get();

        return ResponseEntity.ok(category);
    }

    @Override
    @Transactional
    public ResponseEntity<?> getByUserId() {
        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Checks if users have posts
        if (user.getPosts().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        // Get user posts with comments and likes
        List<Post> posts = new ArrayList<>(user.getPosts()); // triggers lazy loading
        List<PostResponse> responses = PostBuilder.generateUserPosts(posts);
        return ResponseEntity.ok(responses);

    }
}
