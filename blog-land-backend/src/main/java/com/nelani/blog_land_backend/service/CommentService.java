package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.dto.CommentDto;
import com.nelani.blog_land_backend.response.CommentResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommentService {
    long getCountByPostId(Long postId);

    Page<CommentResponse> getByPostId(Long postId, int page, int size);

    List<CommentResponse> getByUserId(long postId);

    void addComment(CommentDto commentDto);

    void updateComment(CommentDto commentDto);

    void deleteComment(Long id);
}
