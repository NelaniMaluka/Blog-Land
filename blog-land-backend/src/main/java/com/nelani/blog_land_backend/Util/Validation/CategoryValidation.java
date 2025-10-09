package com.nelani.blog_land_backend.util.validation;

import com.nelani.blog_land_backend.model.Category;
import com.nelani.blog_land_backend.repository.CategoryRepository;
import org.springframework.stereotype.Component;

@Component
public class CategoryValidation {
    private final CategoryRepository categoryRepository;

    public CategoryValidation(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category assertCategoryExists(long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category does not exist."));
    }

}
