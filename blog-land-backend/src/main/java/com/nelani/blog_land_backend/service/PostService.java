package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.response.PostResponse;

import java.util.List;
import java.util.UUID;

public interface PostService {
    void incrementViews(UUID postId);

    List<PostResponse> searchByKeyword(String query);

    List<PostResponse> getLatestPost(int page, int size);

}
