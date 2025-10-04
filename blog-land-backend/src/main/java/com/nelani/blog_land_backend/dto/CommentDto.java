package com.nelani.blog_land_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentDto {

        private Long id;

        @NotBlank(message = "Comment content cannot be blank")
        private String content;

        @NotNull(message = "Post ID is required")
        @Min(value = 1, message = "Post ID must be greater than 0")
        private Long postId;
}
