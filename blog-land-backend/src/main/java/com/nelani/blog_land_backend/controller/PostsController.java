package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.Util.Builders.PostBuilder;
import com.nelani.blog_land_backend.Util.Validation.PostValidation;
import com.nelani.blog_land_backend.dto.CategoryWithPostsDTO;
import com.nelani.blog_land_backend.dto.PostDto;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.response.CategoryPostGroupResponse;
import com.nelani.blog_land_backend.response.PostResponse;
import com.nelani.blog_land_backend.service.PostService;

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
    public ResponseEntity<?> searchPosts(@RequestParam String query) {
            List<PostResponse> rankedResults =postService.searchByKeyword(query);
            return ResponseEntity.ok(rankedResults);
    }

    @GetMapping("/get/random-post")
    public ResponseEntity<?> getRandomPost() {
            Post post = postRepository.findRandomPost();  // Gets random post

            // Formats the random post and returns it
            PostResponse response = PostBuilder.generateUserPostWithUserInfo(post);
            return ResponseEntity.ok(response);
    }

    @GetMapping("/get/top-post")
    public ResponseEntity<?> getTopPost() {
            List<CategoryWithPostsDTO> posts = postService.getTopCategoriesWithPosts();  // Gets random post
            List<CategoryPostGroupResponse> response = PostBuilder.generateUserPostWithUserInfo(posts);
            
            return ResponseEntity.ok(response);
    }

    @GetMapping("/get/latest-post")
    public ResponseEntity<?> getLatestPost(@RequestParam int page, @RequestParam int size) {
            List<PostResponse> postResponses = postService.getLatestPost(page, size);
            return ResponseEntity.ok(postResponses);
    }

    @GetMapping("/get/popular-post")
    public ResponseEntity<?> getTrendingPost(@RequestParam int page, @RequestParam int size) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Post> popularPosts = postRepository.findAllByOrderByViewCountDesc(pageable);

            Page<PostResponse> responsePage = popularPosts.map(PostBuilder::generatePost);
            return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/get/post/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id) {
            // Checks if the post exists
            Optional<Post> post = postRepository.findById(id);
            PostValidation.assertPostExists(post);

            PostResponse response = PostBuilder.generateUserPostWithUserInfo(post.get());
            return ResponseEntity.ok(response);
    }

    @GetMapping("/get/posts")
    public ResponseEntity<?> getAllPosts(@RequestParam int page, @RequestParam int size, @RequestParam String order) {
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
            Page<PostResponse> responsePage= postService.getByCategoryId(categoryId, page, size, order);
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
            return ResponseEntity.ok().build();
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
    public ResponseEntity<?> deleteUserPost(@RequestBody PostDto postDto) {
            postService.deletePost(postDto);
            return ResponseEntity.ok("Success, Your post was successfully deleted");
    }

}
