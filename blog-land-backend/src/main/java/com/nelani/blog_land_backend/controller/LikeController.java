package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @GetMapping("/get/post-likes/{postId}")
    public ResponseEntity<?> getPostLikesCount(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.getPostLikesCount(postId));
    }

    @GetMapping("/get-user-likes")
    public ResponseEntity<?> getUserLikes() {
        return ResponseEntity.ok(likeService.getUserLikes());
    }

    @PostMapping("/add-like/{postId}")
    @Transactional
    public ResponseEntity<?> addLike(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.addLike(postId));
    }

    @DeleteMapping("/remove-like")
    @Transactional
    public ResponseEntity<?> removeLike(@RequestParam Long likeId) {
        return ResponseEntity.ok(likeService.removeLike(likeId));
    }
}
