package com.nelani.blog_land_backend.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "PostResponse", description = "Represents a blog post with its metadata")
public record PostResponse(
                @Schema(description = "Unique identifier of the post", example = "550e8400-e29b-41d4-a716-446655440000") UUID id,

                @Schema(description = "Title of the post", example = "Introduction to Spring Boot") String title,

                @Schema(description = "Full content of the post", example = "Spring Boot simplifies...") String content,

                @Schema(description = "Estimated read time in minutes", example = "5") int readTime,

                @Schema(description = "Date and time when the post was created", example = "2025-11-06T14:30:00") LocalDateTime createdAt,

                @Schema(description = "Date and time when the post was last updated", example = "2025-11-07T10:20:00") LocalDateTime updatedAt,

                @Schema(description = "Category ID of the post", example = "123e4567-e89b-12d3-a456-426614174000") UUID categoryId,

                @Schema(description = "Short summary of the post", example = "Learn the basics of Spring Boot") String summary,

                @Schema(description = "URL of the post's image", example = "https://example.com/images/post1.jpg") String postImgUrl,

                @Schema(description = "Number of views for the post", example = "120") Long views,

                @Schema(description = "Number of likes for the post", example = "45") Long likes,

                @Schema(description = "References mentioned in the post", example = "https://spring.io") String references,

                @Schema(description = "Source of the content if external", example = "Medium Article") String source,

                @Schema(description = "Relevance or ranking score", example = "85") int score,

                @Schema(description = "ID of the user who created the post", example = "publicId") String userId,

                @Schema(description = "Flag indicating if the post is a draft", example = "false") boolean isDraft

) {
}
