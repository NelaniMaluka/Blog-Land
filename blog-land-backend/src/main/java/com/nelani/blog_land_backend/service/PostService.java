package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.dto.CategoryWithPostsDTO;
import com.nelani.blog_land_backend.dto.PostDto;
import com.nelani.blog_land_backend.response.PostResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService {
    void incrementViews(Long postId);

    List<CategoryWithPostsDTO> getTopCategoriesWithPosts();

    List<PostResponse> searchByKeyword(String query);

    Page<PostResponse> getByCategoryId(Long categoryId, int page, int size, String order);

    List<PostResponse> getLatestPost(int page, int size);

    Page<PostResponse> getByUserId(int page, int size);

    void addPost(PostDto postDto);

    void updatePost(PostDto postDto);

    void deletePost(Long id);
}
