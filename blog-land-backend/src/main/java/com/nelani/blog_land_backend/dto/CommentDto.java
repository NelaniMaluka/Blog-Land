package com.nelani.blog_land_backend.dto;

import lombok.Data;

@Data
public class CommentDto {
        private Long id;
        private String content;
        private Long postId;
}
