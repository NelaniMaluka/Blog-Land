package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.Util.PostBuilder;
import com.nelani.blog_land_backend.Util.ResponseBuilder;
import com.nelani.blog_land_backend.dto.PostDto;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.response.PostResponse;
import com.nelani.blog_land_backend.service.PostService;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;



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
    public ResponseEntity<?> getAllPosts(@RequestParam int page, @RequestParam int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Post> postPage = postRepository.findAll(pageable);

            Page<PostResponse> responsePage = postPage.map(PostBuilder::generateUserPostWithUserInfo);
            return ResponseEntity.ok(responsePage);
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

    @GetMapping("/get-all/category")
    public ResponseEntity<?> getAllPostsByCategory(@RequestParam Long categoryId, @RequestParam int page, @RequestParam int size) {
        try {
            return postService.getByCategoryId(categoryId , page, size);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.invalid("Validation Error", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

    @GetMapping("/get-user-posts")
    public ResponseEntity<?> getAllPostsByUserId(@RequestParam int page, @RequestParam int size) {
        try {
            return postService.getByUserId(page, size);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.invalid("Validation Error", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

    @PostMapping("/add-user-posts")
    public ResponseEntity<?> addAllPostsByUserId(@RequestBody PostDto postDto) {
        try {
            return postService.addPost(postDto);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.invalid("Validation Error", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

    @PutMapping("/update-user-post")
    public ResponseEntity<?> updateUserPost(@RequestBody PostDto postDto) {
        try {
            return postService.updatePost(postDto);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.invalid("Validation Error", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

    @DeleteMapping("/delete-user-post")
    public ResponseEntity<?> deleteUserPost(@RequestBody PostDto postDto) {
        try {
            return postService.deletePost(postDto);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.invalid("Validation Error", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

}
