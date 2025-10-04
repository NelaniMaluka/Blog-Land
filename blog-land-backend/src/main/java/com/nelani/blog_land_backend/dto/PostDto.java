package com.nelani.blog_land_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title cannot exceed 150 characters")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Summary is required")
    @Size(max = 300, message = "Summary cannot exceed 300 characters")
    private String summary;

    @NotBlank(message = "Image URL is required")
    @Pattern(regexp = "^(http(s?):)([/|.|\\w|\\s|-])*\\.(?:jpg|jpeg|png|gif)$", message = "Image URL must be a valid link ending with jpg, jpeg, png, or gif")
    private String imgUrl;

    private LocalDateTime updatedAt;
    private String references;
    private boolean draft;
    private LocalDateTime scheduledAt;

}
