package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.dto.CommentDto;
import com.nelani.blog_land_backend.response.CommentResponse;
import org.springframework.data.domain.Page;

public interface CommentService {
    Page<CommentResponse> getByPostId(Long postId, int page, int size);

    Page<CommentResponse> getByUserId(int page, int size);

    void addComment(CommentDto commentDto);

    void updateComment(CommentDto commentDto);

    void deleteComment(CommentDto commentDto);
}
