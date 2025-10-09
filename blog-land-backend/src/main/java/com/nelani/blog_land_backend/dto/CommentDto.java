package com.nelani.blog_land_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentDto(

                Long id,

                @NotBlank(message = "Comment content cannot be blank") String content,

                @NotNull(message = "Post ID is required") @Min(value = 1, message = "Post ID must be greater than 0") Long postId) {
}
