package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.dto.PostDto;
import com.nelani.blog_land_backend.response.PostResponse;
import com.nelani.blog_land_backend.service.UserPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api")
@Tag(name = "User Posts Controller", description = "Endpoints for managing posts created by authenticated users")
public class UserPostsController {

        private final UserPostService userPostService;

        public UserPostsController(UserPostService userPostService) {
                this.userPostService = userPostService;
        }

        @GetMapping("/user/posts")
        @Operation(summary = "Get all posts by authenticated user", description = "Retrieves a paginated list of all posts created by the currently authenticated user.")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user's posts", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class)))
        @PreAuthorize("hasAuthority('post:read')")
        public ResponseEntity<Page<PostResponse>> getAllPostsByUserId(
                        @RequestParam int page,
                        @RequestParam int size) {
                var responsePage = userPostService.getByUserId(page, size);
                return ResponseEntity.ok(responsePage);
        }

        @PostMapping("/user/posts/add")
        @Operation(summary = "Add a new post for the authenticated user", description = "Creates a new post for the currently authenticated user using the provided post data.")
        @ApiResponse(responseCode = "201", description = "Successfully added post", content = @Content(mediaType = "application/json", schema = @Schema(example = "\"Success, Your post was successfully added\"")))
        @PreAuthorize("hasAuthority('post:write')")
        public ResponseEntity<String> addPostsByUserId(@RequestBody PostDto postDto) {
                userPostService.addPost(postDto);
                return ResponseEntity.status(HttpStatus.CREATED).body("Success, Your post was successfully added");
        }

        @PutMapping("/user/posts/update")
        @Operation(summary = "Update a user's post", description = "Updates an existing post of the currently authenticated user using the provided post data.")
        @ApiResponse(responseCode = "200", description = "Successfully updated post", content = @Content(mediaType = "application/json", schema = @Schema(example = "\"Success, Your post was successfully updated\"")))
        @PreAuthorize("hasAuthority('post:write')")
        public ResponseEntity<String> updateUserPost(@RequestBody PostDto postDto) {
                userPostService.updatePost(postDto);
                return ResponseEntity.ok("Success, Your post was successfully updated");
        }

        @DeleteMapping("/user/posts/remove")
        @Operation(summary = "Delete a user's post", description = "Deletes a specific post of the currently authenticated user by providing its unique ID.")
        @ApiResponse(responseCode = "204", description = "Successfully deleted post")
        @PreAuthorize("hasAuthority('post:delete')")
        public ResponseEntity<String> deleteUserPost(
                        @RequestParam UUID id) {
                userPostService.deletePost(id);
                return ResponseEntity.noContent().build();
        }
}
