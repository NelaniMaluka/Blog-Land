package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.response.PostResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface CategoryService {
    Page<PostResponse> getByCategoryId(UUID categoryId, int page, int size, String order);
}
