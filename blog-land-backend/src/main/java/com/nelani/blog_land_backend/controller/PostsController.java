package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.Util.ResponseBuilder;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.service.PostService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/post")
public class PostsController {

    private final PostRepository postRepository;
    private final PostService postService;

    public PostsController(PostRepository postRepository, PostService postService) {
        this.postRepository = postRepository;
        this.postService = postService;
    }

    @GetMapping("/get-all/posts")
    public ResponseEntity<?> getAllPosts() {
        try {
            return ResponseEntity.ok(postRepository.findAll());
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

    @GetMapping("/get-all/category")
    public ResponseEntity<?> getAllPostsByCategory(@RequestParam Long categoryId) {
        try {
            return postService.getByCategoryId(categoryId);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.invalid("Validation Error", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

    @GetMapping("/get/user-posts")
    public ResponseEntity<?> getAllPostsByUserId() {
        try {
            return postService.getByUserId();
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.invalid("Validation Error", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

}
