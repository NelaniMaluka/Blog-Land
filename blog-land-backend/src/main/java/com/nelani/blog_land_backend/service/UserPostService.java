package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.dto.PostDto;
import com.nelani.blog_land_backend.response.PostResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface UserPostService {
    Page<PostResponse> getByUserId(int page, int size);

    void addPost(PostDto postDto);

    void updatePost(PostDto postDto);

    void deletePost(UUID id);
}
