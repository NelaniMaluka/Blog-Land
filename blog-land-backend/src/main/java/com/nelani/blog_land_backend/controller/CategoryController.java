package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.dto.CategoryDto;
import com.nelani.blog_land_backend.repository.CategoryRepository;

import com.nelani.blog_land_backend.repository.PostRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

    public CategoryController(CategoryRepository categoryRepository, PostRepository postRepository) {
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
    }

    @GetMapping
    public ResponseEntity<?> getCategories() {
            List<CategoryDto> categoryDtos = categoryRepository.findAll()
                    .stream()
                    .map(category -> {
                        int postCount = postRepository.countByCategoryId(category.getId());
                        return new CategoryDto(category, postCount);
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(categoryDtos);
    }
}
