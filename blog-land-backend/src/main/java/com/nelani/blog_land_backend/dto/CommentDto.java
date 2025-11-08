package com.nelani.blog_land_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentDto(
        @NotBlank(message = "Comment content cannot be blank") String content) {
}
