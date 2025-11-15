package com.nelani.blog_land_backend.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(name = "CategoryResponse", description = "Represents a category with its metadata and post count")
public record CategoryResponse(
                @Schema(description = "Unique identifier of the category", example = "550e8400-e29b-41d4-a716-446655440000") UUID id,

                @Schema(description = "Name of the category", example = "Technology") String name,

                @Schema(description = "Number of posts under this category", example = "12") int postCount) {
}
