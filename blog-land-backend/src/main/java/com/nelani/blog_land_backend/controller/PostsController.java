package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.mapper.PostBuilder;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.response.PostResponse;
import com.nelani.blog_land_backend.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api")
@Tag(name = "Posts Controller", description = "Endpoints for managing and retrieving posts")
public class PostsController {

        private final PostRepository postRepository;
        private final PostService postService;

        public PostsController(PostRepository postRepository, PostService postService) {
                this.postRepository = postRepository;
                this.postService = postService;
        }

        @GetMapping("/public/posts/search")
        @Operation(summary = "Search posts by keyword", description = "Retrieves a list of posts ranked by relevance based on the provided keyword.")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved search results", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class)))
        public ResponseEntity<List<PostResponse>> searchPosts(
                        @RequestParam String keyword) {
                List<PostResponse> rankedResults = postService.searchByKeyword(keyword);
                return ResponseEntity.ok(rankedResults);
        }

        @GetMapping("/public/posts/random")
        @Operation(summary = "Get a random post", description = "Retrieves a single randomly selected post from the database. "
                        +
                        "This endpoint is publicly accessible and returns post details with user information.")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved a random post", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class)))
        public ResponseEntity<PostResponse> getRandomPost() {
                Post post = postRepository.findRandomPost();

                PostResponse response = PostBuilder.generateUserPostWithUserInfo(post);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/public/posts/related")
        @Operation(summary = "Get related (random) posts", description = "Retrieves a list of randomly selected posts that can be used as related or suggested content. "
                        +
                        "This endpoint is publicly accessible and returns multiple post previews.")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved related posts", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class)))
        public ResponseEntity<List<PostResponse>> getRandomPosts() {
                List<Post> posts = postRepository.findRandomPosts();

                List<PostResponse> response = posts.stream()
                                .map(PostBuilder::generatePost)
                                .toList();

                return ResponseEntity.ok(response);
        }

        @GetMapping("/public/posts/latest")
        @Operation(summary = "Get the latest posts", description = "Retrieves a paginated list of the most recently published posts. "
                        +
                        "This endpoint is publicly accessible and supports pagination parameters.")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the latest posts", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class)))
        public ResponseEntity<List<PostResponse>> getLatestPost(
                        @RequestParam int page,
                        @RequestParam int size) {
                List<PostResponse> postResponses = postService.getLatestPost(page, size);
                return ResponseEntity.ok(postResponses);
        }

        @GetMapping("/public/posts/popular")
        @Operation(summary = "Get popular (trending) posts", description = "Retrieves a paginated list of trending or most popular posts. "
                        +
                        "This endpoint is publicly accessible and uses caching to improve performance.")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved popular posts", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class)))
        @Cacheable(value = "trendingPosts", key = "#page + '_' + #size")
        public ResponseEntity<Page<PostResponse>> getTrendingPost(
                        @RequestParam int page,
                        @RequestParam int size) {
                Pageable pageable = PageRequest.of(page, size);
                Page<Post> popularPosts = postRepository.findTrendingPosts(pageable);

                Page<PostResponse> responsePage = popularPosts.map(PostBuilder::generatePost);
                return ResponseEntity.ok(responsePage);
        }

        @GetMapping("/public/posts/{postId}")
        @Operation(summary = "Get a post by ID", description = "Retrieves a single post by its unique ID. " +
                        "This endpoint is publicly accessible and uses caching to optimize performance.")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the post", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class)))
        @Cacheable(value = "post", key = "#postId")
        public ResponseEntity<PostResponse> getPost(
                        @PathVariable UUID postId) {
                // Checks if the post exists
                Post post = postRepository.findById(postId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

                PostResponse response = PostBuilder.generateUserPostWithUserInfo(post);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/public/posts")
        @Operation(summary = "Get all posts with pagination and order", description = "Retrieves a paginated list of all posts. "
                        +
                        "Supports ordering by latest or oldest posts. " +
                        "This endpoint is publicly accessible and uses caching to improve performance.")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all posts", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class)))
        @Cacheable(value = "allPosts", key = "#page + '_' + #size + '_' + #order")
        public ResponseEntity<Page<PostResponse>> getAllPosts(
                        @RequestParam int page,
                        @RequestParam int size,
                        @RequestParam String order) {
                String setOrder = (order == null || !order.equals("oldest")) ? "latest" : "oldest";

                Sort.Direction direction = setOrder.equals("latest") ? Sort.Direction.DESC : Sort.Direction.ASC;
                Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "createdAt"));
                Page<Post> postPage = postRepository.findAll(pageable);

                Page<PostResponse> responsePage = postPage.map(PostBuilder::generatePost);
                return ResponseEntity.ok(responsePage);
        }

        @PostMapping("/public/posts/{postId}/view")
        @Operation(summary = "Increment post view count", description = "Increments the view count for the specified post. "
                        +
                        "This endpoint does not return any content.")
        @ApiResponse(responseCode = "204", description = "Successfully incremented view count, no content returned")
        public ResponseEntity<Void> incrementViewCount(
                        @PathVariable("postId") UUID postId) {
                postService.incrementViews(postId);
                return ResponseEntity.noContent().build();
        }

}
