package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.dto.PostDto;
import org.springframework.http.ResponseEntity;

public interface PostService {
    ResponseEntity<?> getByCategoryId(Long categoryId, int page, int size);

    ResponseEntity<?> getLatestPost( int page, int size);

    ResponseEntity<?> getByUserId(int page, int size);

    ResponseEntity<?> addPost(PostDto postDto);

    ResponseEntity<?> updatePost(PostDto postDto);

    ResponseEntity<?> deletePost(PostDto postDto);
}
