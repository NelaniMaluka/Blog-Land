package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api")
@Tag(name = "Like Controller", description = "Manages likes on posts.")
public class LikeController {

        private final LikeService likeService;

        public LikeController(LikeService likeService) {
                this.likeService = likeService;
        }

        @Operation(summary = "Get like count for a post", description = "Returns the total number of likes for the specified post.")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved like count", content = @Content(schema = @Schema(example = "{\"likesCount\": 42}")))
        @GetMapping("/public/posts/{postId}/likes")
        public ResponseEntity<?> getPostLikesCount(
                        @PathVariable("postId") @NotNull(message = "Post ID is required") UUID postId) {
                return ResponseEntity.ok(Map.of("likesCount", likeService.getPostLikesCount(postId)));
        }

        @Operation(summary = "Get all likes by the authenticated user", description = "Fetches all posts liked by the currently logged-in user.")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user likes", content = @Content(schema = @Schema(example = "[{\"postId\": \"uuid\", \"likedAt\": \"2025-11-03T08:00:00\"}]")))
        @GetMapping("/user/posts/likes")
        @PreAuthorize("hasAuthority('like:read')")
        public ResponseEntity<?> getUserLikes() {
                return ResponseEntity.ok(likeService.getUserLikes());
        }

        @Operation(summary = "Add a like to a post", description = "Allows an authenticated user to like a specific post by its ID.")
        @ApiResponse(responseCode = "201", description = "Like added successfully", content = @Content(schema = @Schema(example = "{\"message\": \"Like added successfully\", \"postId\": \"uuid\"}")))
        @PostMapping("/user/posts/{postId}/likes")
        @PreAuthorize("hasAuthority('like:write')")
        public ResponseEntity<?> addLike(
                        @PathVariable("postId") @NotNull(message = "Post ID is required") UUID postId) {
                likeService.addLike(postId);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(Map.of("message", "Like added successfully", "postId", postId));
        }

        @Operation(summary = "Remove a like from a post", description = "Removes a previously added like by its ID for the authenticated user.")
        @ApiResponse(responseCode = "204", description = "Like removed successfully", content = @Content(schema = @Schema(example = "{\"message\": \"Like removed successfully\"}")))
        @DeleteMapping("/user/posts/likes/{likeId}")
        @PreAuthorize("hasAuthority('like:delete')")
        public ResponseEntity<?> removeLike(
                        @PathVariable("likeId") @NotNull(message = "Like ID is required") UUID likeId) {
                likeService.removeLike(likeId);
                return ResponseEntity.noContent().build();
        }
}
