package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.Util.Builders.PostBuilder;
import com.nelani.blog_land_backend.Util.Builders.ResponseBuilder;
import com.nelani.blog_land_backend.Util.Validation.PostValidation;
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
        try {
            List<PostResponse> rankedResults =postService.searchByKeyword(query);
            return ResponseEntity.ok(rankedResults);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.invalid("Validation Error", e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.ok(e);
        }
    }

    @GetMapping("/get/random-post")
    public ResponseEntity<?> getRandomPost() {
        try {
            Post post = postRepository.findRandomPost();  // Gets random post

            // Formats the random post and returns it
            PostResponse response = PostBuilder.generateUserPostWithUserInfo(post);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

    @GetMapping("/get/latest-post")
    public ResponseEntity<?> getLatestPost(@RequestParam int page, @RequestParam int size) {
        try {
            List<PostResponse> postResponses = postService.getLatestPost(page, size);
            return ResponseEntity.ok(postResponses);
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

    @GetMapping("/get/popular-post")
    public ResponseEntity<?> getTrendingPost(@RequestParam int page, @RequestParam int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Post> popularPosts = postRepository.findAllByOrderByViewCountDesc(pageable);

            Page<PostResponse> responsePage = popularPosts.map(PostBuilder::generatePost);
            return ResponseEntity.ok(responsePage);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.invalid("Validation Error", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

    @GetMapping("/get/{id}/post")
    public ResponseEntity<?> getPost(@PathVariable Long id) {
        try {
            // Checks if the post exists
            Optional<Post> post = postRepository.findById(id);
            PostValidation.assertPostExists(post);

            PostResponse response = PostBuilder.generateUserPostWithUserInfo(post.get());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

    @GetMapping("/get/posts")
    public ResponseEntity<?> getAllPosts(@RequestParam int page, @RequestParam int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Post> postPage = postRepository.findAllByOrderByCreatedAtDesc(pageable);

            Page<PostResponse> responsePage = postPage.map(PostBuilder::generatePost);
            return ResponseEntity.ok(responsePage);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.invalid("Validation Error", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

    @GetMapping("/get/category")
    public ResponseEntity<?> getAllPostsByCategory(@RequestParam Long categoryId, @RequestParam int page,
            @RequestParam int size,@RequestParam String order) {
        try {
            Page<PostResponse> responsePage= postService.getByCategoryId(categoryId, page, size, order);
            return ResponseEntity.ok(responsePage);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.invalid("Validation Error", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

    @GetMapping("/get-user-posts")
    public ResponseEntity<?> getAllPostsByUserId(@RequestParam int page, @RequestParam int size) {
        try {
            Page<PostResponse> responsePage = postService.getByUserId(page, size);
            return ResponseEntity.ok(responsePage);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.invalid("Validation Error", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

    @PostMapping("/get/posts/{postId}/view")
    public ResponseEntity<?> incrementViewCount(@PathVariable Long postId) {
        try {
            postService.incrementViews(postId);
            return ResponseEntity.ok().build(); // still OK with void response
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.invalid("Validation Error", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

    @PostMapping("/add-user-posts")
    public ResponseEntity<?> addAllPostsByUserId(@RequestBody PostDto postDto) {
        try {
            postService.addPost(postDto);
            return ResponseEntity.ok("Success, Your post was successfully added");
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.invalid("Validation Error", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

    @PutMapping("/update-user-post")
    public ResponseEntity<?> updateUserPost(@RequestBody PostDto postDto) {
        try {
            postService.updatePost(postDto);
            return ResponseEntity.ok("Success, Your post was successfully updated");
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.invalid("Validation Error", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

    @DeleteMapping("/delete-user-post")
    public ResponseEntity<?> deleteUserPost(@RequestBody PostDto postDto) {
        try {
            postService.deletePost(postDto);
            return ResponseEntity.ok("Success, Your post was successfully deleted");
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.invalid("Validation Error", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

}
