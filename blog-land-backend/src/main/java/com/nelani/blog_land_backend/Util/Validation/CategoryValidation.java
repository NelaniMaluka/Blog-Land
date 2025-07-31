package com.nelani.blog_land_backend.Util.Validation;

import com.nelani.blog_land_backend.model.Category;

import java.util.Optional;

public class CategoryValidation {

    public static void assertCategoryExists(Optional<Category> category) {
        if (category.isEmpty()) {
            throw new IllegalArgumentException("Category does not exist.");
        }
    }

}
