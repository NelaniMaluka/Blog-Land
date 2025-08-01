package com.nelani.blog_land_backend.dto;

import com.nelani.blog_land_backend.model.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryWithPostsDTO {
    private Long categoryId;
    private String categoryName;
    private List<Post> posts;
}

