package com.nelani.blog_land_backend.dto;

import com.nelani.blog_land_backend.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;
    private int postCount;

    public CategoryDto(Category category, int postCount) {
        this.id = category.getId();
        this.name = category.getName();
        this.postCount = postCount;
    }
}

