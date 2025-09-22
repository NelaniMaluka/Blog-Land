package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.Util.Builders.PostBuilder;
import com.nelani.blog_land_backend.Util.Validation.PostValidation;
import com.nelani.blog_land_backend.dto.PostDto;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.response.PostResponse;
import com.nelani.blog_land_backend.service.PostService;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/post")
public class PostsController {

        private final PostRepository postRepository;
        private final PostService postService;

        public PostsController(PostRepository postRepository, PostService postService) {
                this.postRepository = postRepository;
                this.postService = postService;
        }

        @GetMapping("/api/search")
        public ResponseEntity<?> searchPosts(@RequestParam String keyword) {
                List<PostResponse> rankedResults = postService.searchByKeyword(keyword);
                return ResponseEntity.ok(rankedResults);
        }

        @GetMapping("/get/random-post")
        public ResponseEntity<?> getRandomPost() {
                Post post = postRepository.findRandomPost();

                // Formats the random post and returns it
                PostResponse response = PostBuilder.generateUserPostWithUserInfo(post);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/get/random-posts")
        public ResponseEntity<?> getRandomPosts() {
                List<Post> posts = postRepository.findRandomPosts();

                // Formats the random posts and returns them
                List<PostResponse> response = posts.stream()
                        .map(PostBuilder::generatePost)
                        .toList();

                return ResponseEntity.ok(response);
        }

        @GetMapping("/get/latest-post")
        public ResponseEntity<?> getLatestPost(@RequestParam int page, @RequestParam int size) {
                List<PostResponse> postResponses = postService.getLatestPost(page, size);
                return ResponseEntity.ok(postResponses);
        }

        @GetMapping("/get/popular-post")
        @Cacheable(value = "trendingPosts", key = "#page + '_' + #size")
        public ResponseEntity<?> getTrendingPost(@RequestParam int page, @RequestParam int size) {
                Pageable pageable = PageRequest.of(page, size);
                Page<Post> popularPosts = postRepository.findTrendingPosts(pageable);

                Page<PostResponse> responsePage = popularPosts.map(PostBuilder::generatePost);
                return ResponseEntity.ok(responsePage);
        }


        @GetMapping("/get/post/{id}")
        @Cacheable(value = "post", key = "#id")
        public ResponseEntity<?> getPost(@PathVariable Long id) {
                // Checks if the post exists
                Optional<Post> post = postRepository.findById(id);
                PostValidation.assertPostExists(post);

                PostResponse response = PostBuilder.generateUserPostWithUserInfo(post.get());
                return ResponseEntity.ok(response);
        }

        @GetMapping("/get/posts")
        @Cacheable(value = "allPosts", key = "#page + '_' + #size + '_' + #order")
        public ResponseEntity<?> getAllPosts(@RequestParam int page, @RequestParam int size,
                        @RequestParam String order) {
                String setOrder = (order == null || !order.equals("oldest")) ? "latest" : "oldest";

                Sort.Direction direction = setOrder.equals("latest") ? Sort.Direction.DESC : Sort.Direction.ASC;
                Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "createdAt"));
                Page<Post> postPage = postRepository.findAll(pageable);

                Page<PostResponse> responsePage = postPage.map(PostBuilder::generatePost);
                return ResponseEntity.ok(responsePage);
        }

        @GetMapping("/get/category")
        public ResponseEntity<?> getAllPostsByCategory(@RequestParam Long categoryId, @RequestParam int page,
                        @RequestParam int size, @RequestParam String order) {
                Page<PostResponse> responsePage = postService.getByCategoryId(categoryId, page, size, order);
                return ResponseEntity.ok(responsePage);
        }

        @GetMapping("/get-user-posts")
        public ResponseEntity<?> getAllPostsByUserId(@RequestParam int page, @RequestParam int size) {
                Page<PostResponse> responsePage = postService.getByUserId(page, size);
                return ResponseEntity.ok(responsePage);
        }

        @PostMapping("/get/posts/view/{postId}")
        public ResponseEntity<?> incrementViewCount(@PathVariable Long postId) {
                postService.incrementViews(postId);
                return ResponseEntity.ok("Successfully added your view");
        }

        @PostMapping("/add-user-posts")
        public ResponseEntity<?> addAllPostsByUserId(@RequestBody PostDto postDto) {
                postService.addPost(postDto);
                return ResponseEntity.ok("Success, Your post was successfully added");
        }

        @PutMapping("/update-user-post")
        public ResponseEntity<?> updateUserPost(@RequestBody PostDto postDto) {
                postService.updatePost(postDto);
                return ResponseEntity.ok("Success, Your post was successfully updated");
        }

        @DeleteMapping("/delete-user-post")
        public ResponseEntity<?> deleteUserPost(@RequestParam Long id) {
                postService.deletePost(id);
                return ResponseEntity.ok("Success, Your post was successfully deleted");
        }

}
