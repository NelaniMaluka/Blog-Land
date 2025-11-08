package com.nelani.blog_land_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostDto(
                UUID id,

                @NotBlank(message = "Title is required") @Size(max = 150, message = "Title cannot exceed 150 characters") String title,

                @NotBlank(message = "Content is required") String content,

                @NotNull(message = "Category ID is required") UUID categoryId,

                @NotBlank(message = "Summary is required") @Size(max = 300, message = "Summary cannot exceed 300 characters") String summary,

                @NotBlank(message = "Image URL is required") @Pattern(regexp = "^(http(s?):)([/|.|\\w|\\s|-])*\\.(?:jpg|jpeg|png|gif)$", message = "Image URL must be a valid link ending with jpg, jpeg, png, or gif") String imgUrl,

                LocalDateTime updatedAt,
                String references,
                boolean draft,
                LocalDateTime scheduledAt) {
}
