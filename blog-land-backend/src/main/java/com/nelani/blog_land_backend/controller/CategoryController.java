package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.repository.CategoryRepository;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.response.CategoryResponse;

import com.nelani.blog_land_backend.response.PostResponse;
import com.nelani.blog_land_backend.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api")
@Tag(name = "Category Controller", description = "Endpoints for retrieving categories and posts by category")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private final CategoryService categoryService;

    public CategoryController(CategoryRepository categoryRepository, PostRepository postRepository,
            CategoryService categoryService) {
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.categoryService = categoryService;
    }

    @Operation(summary = "Get all categories with post counts")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of categories")
    @GetMapping("public/posts/categories")
    @Cacheable(value = "categories")
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        List<CategoryResponse> categoryDtos = categoryRepository.findAll()
                .stream()
                .map(category -> {
                    int postCount = postRepository.countByCategoryId(category.getId());
                    return new CategoryResponse(category.getId(), category.getName(), postCount);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(categoryDtos);
    }

    @GetMapping("/public/posts/categories/{categoryId}")
    @Operation(summary = "Get posts by category", description = "Retrieves a paginated list of posts belonging to a specific category. "
            +
            "Supports ordering by latest or oldest posts.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved posts by category", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class)))
    public ResponseEntity<Page<PostResponse>> getAllPostsByCategory(
            @PathVariable UUID categoryId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String order) {
        Page<PostResponse> responsePage = categoryService.getByCategoryId(categoryId, page, size, order);
        return ResponseEntity.ok(responsePage);
    }
}
