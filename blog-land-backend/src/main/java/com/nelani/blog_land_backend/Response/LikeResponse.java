package com.nelani.blog_land_backend.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Schema(name = "LikeResponse", description = "Represents a like on a post including metadata")
public record LikeResponse(
                @Schema(description = "Unique identifier of the like", example = "550e8400-e29b-41d4-a716-446655440000") UUID likeId,

                @Schema(description = "Timestamp when the like was made", example = "2025-11-06T14:30:00") LocalDateTime likedAt,

                @Schema(description = "ID of the post that was liked", example = "123e4567-e89b-12d3-a456-426614174000") UUID postId

) {
}
