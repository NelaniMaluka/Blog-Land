package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.dto.CommentDto;
import org.springframework.http.ResponseEntity;

public interface CommentService {
    ResponseEntity<?> getByPostId(Long postId , int page, int size);
    ResponseEntity<?> getByUserId(Long userId , int page, int size);
    ResponseEntity<?> addComment(CommentDto commentDto);
    ResponseEntity<?> updateComment(CommentDto commentDto);
    ResponseEntity<?> deleteComment(CommentDto commentDto);
}
