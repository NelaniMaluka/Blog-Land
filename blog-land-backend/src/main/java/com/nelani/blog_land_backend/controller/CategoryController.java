package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.Util.ResponseBuilder;
import com.nelani.blog_land_backend.dto.CategoryDto;
import com.nelani.blog_land_backend.repository.CategoryRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    public final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public ResponseEntity<?> getCategories(){
        try {
            List<CategoryDto> categoryDtos = categoryRepository.findAll()
                    .stream()
                    .map(CategoryDto::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(categoryDtos);
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }
}
