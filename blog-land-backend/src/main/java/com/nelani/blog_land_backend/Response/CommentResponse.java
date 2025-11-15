package com.nelani.blog_land_backend.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CommentResponse", description = "Represents a comment on a post along with metadata")
public record CommentResponse(
                @Schema(description = "Unique identifier of the comment", example = "550e8400-e29b-41d4-a716-446655440000") UUID id,

                @Schema(description = "Content of the comment", example = "This is a great post!") String content,

                @Schema(description = "Date and time when the comment was created", example = "2025-11-06T14:30:00") LocalDateTime createdAt,

                @Schema(description = "ID of the user who created the comment", example = "publicId") String userId

) {
}
