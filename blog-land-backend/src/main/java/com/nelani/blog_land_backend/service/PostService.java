package com.nelani.blog_land_backend.service;

import org.springframework.http.ResponseEntity;

public interface PostService {
    ResponseEntity<?> getByCategoryId(Long categoryId);

    ResponseEntity<?> getByUserId();
}
