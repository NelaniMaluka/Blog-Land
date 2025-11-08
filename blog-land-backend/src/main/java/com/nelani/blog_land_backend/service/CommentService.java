package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.dto.CommentDto;
import com.nelani.blog_land_backend.response.CommentResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    long getCountByPostId(UUID postId);

    Page<CommentResponse> getByPostId(UUID postId, int page, int size);

    List<UUID> getByUserId(UUID postId);

    void addComment(UUID postId, CommentDto commentDto);

    void updateComment(UUID postId, UUID commentId, CommentDto commentDto);

    void deleteComment(UUID id);
}
