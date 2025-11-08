package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.dto.CommentDto;
import com.nelani.blog_land_backend.response.CommentResponse;
import com.nelani.blog_land_backend.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api")
@Tag(name = "Comments Controller", description = "Endpoints for managing user comments on posts")
public class CommentController {

        private final CommentService commentService;

        public CommentController(CommentService commentService) {
                this.commentService = commentService;
        }

        @Operation(summary = "Get total comments count for a post")
        @ApiResponse(responseCode = "200", description = "Total comments count retrieved successfully", content = @Content(schema = @Schema(implementation = Long.class)))

        @GetMapping("/public/posts/{postId}/comments/count")
        public ResponseEntity<Long> getCommentsCountByPost(
                        @PathVariable("postId") @NotNull UUID postId) {

                long count = commentService.getCountByPostId(postId);
                return ResponseEntity.ok(count);
        }

        @Operation(summary = "Get paginated comments for a post")
        @ApiResponse(responseCode = "200", description = "Comments retrieved successfully", content = @Content(schema = @Schema(implementation = CommentResponse.class)))
        @GetMapping("/public/posts/{postId}/comments")
        public ResponseEntity<Page<CommentResponse>> getAllCommentsByPost(
                        @PathVariable("postId") @NotNull UUID postId,
                        @RequestParam(defaultValue = "0") @Min(0) int page,
                        @RequestParam(defaultValue = "10") @Positive int size) {

                Page<CommentResponse> responsePage = commentService.getByPostId(postId, page, size);
                return ResponseEntity.ok(responsePage);
        }

        @Operation(summary = "Get all User comment IDs for a post")
        @ApiResponse(responseCode = "200", description = "Comment IDs retrieved successfully", content = @Content(schema = @Schema(implementation = UUID.class)))
        @GetMapping("/user/posts/{postId}/comments/ids")
        @PreAuthorize("hasAuthority('comment:read')")
        public ResponseEntity<List<UUID>> getAllCommentIdsByPost(
                        @PathVariable("postId") @NotNull UUID postId) {

                List<UUID> commentIds = commentService.getByUserId(postId);
                return ResponseEntity.ok(commentIds);
        }

        @Operation(summary = "Add a comment to a post")
        @ApiResponse(responseCode = "200", description = "Comment added successfully")
        @PostMapping("/user/posts/{postId}/comments")
        @PreAuthorize("hasAuthority('comment:write')")
        public ResponseEntity<?> addComment(
                        @PathVariable("postId") @NotNull UUID postId,
                        @RequestBody @Valid CommentDto commentDto) {

                commentService.addComment(postId, commentDto);
                return ResponseEntity.status(HttpStatus.CREATED).body("Comment added successfully");
        }

        @Operation(summary = "Update a comment")
        @ApiResponse(responseCode = "200", description = "Comment updated successfully")
        @PutMapping("/user/posts/{postId}/comments/{commentId}")
        @PreAuthorize("hasAuthority('comment:write')")
        public ResponseEntity<?> updateComment(
                        @PathVariable("postId") @NotNull UUID postId,
                        @PathVariable("commentId") @NotNull UUID commentId,
                        @RequestBody @Valid CommentDto commentDto) {

                commentService.updateComment(postId, commentId, commentDto);
                return ResponseEntity.ok().body("Comment updated successfully");
        }

        @Operation(summary = "Delete a comment")
        @ApiResponse(responseCode = "204", description = "Comment deleted successfully")
        @DeleteMapping("/user/posts/comments/{commentId}")
        @PreAuthorize("hasAuthority('comment:delete')")
        public ResponseEntity<Void> deleteComment(
                        @PathVariable("commentId") @NotNull UUID commentId) {

                commentService.deleteComment(commentId);
                return ResponseEntity.noContent().build();
        }
}
