package com.nelani.blog_land_backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryPostGroupResponse {
    private Long categoryId;
    private String categoryName;
    private List<PostResponse> posts;
}

