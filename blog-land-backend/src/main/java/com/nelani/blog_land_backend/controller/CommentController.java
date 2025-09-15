package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.dto.CommentDto;
import com.nelani.blog_land_backend.response.CommentResponse;
import com.nelani.blog_land_backend.service.CommentService;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

        private final CommentService commentService;

        public CommentController(CommentService commentService) {
                this.commentService = commentService;
        }

        @GetMapping("/get/comments-count")
        public ResponseEntity<?> getCommentsCountByPost(@RequestParam Long postId) {
                long count = commentService.getCountByPostId(postId);
                return ResponseEntity.ok(count);
        }

        @GetMapping("/get/comments")
        public ResponseEntity<?> getAllCommentsByCategory(@RequestParam Long postId, @RequestParam int page,
                        @RequestParam int size) {
                Page<CommentResponse> responsePage = commentService.getByPostId(postId, page, size);
                return ResponseEntity.ok(responsePage);
        }

        @GetMapping("/get-user-comments")
        public ResponseEntity<?> getAllCommentsByUserId(@RequestParam int postId) {
                List<CommentResponse> responsePage = commentService.getByUserId(postId);
                return ResponseEntity.ok(responsePage);
        }

        @PostMapping("/add-user-comment")
        public ResponseEntity<?> addCommentByUserId(@RequestBody CommentDto commentDto) {
                commentService.addComment(commentDto);
                return ResponseEntity.ok("Success, Your comment was successfully added");
        }

        @PutMapping("/update-user-comments")
        public ResponseEntity<?> updateUserComment(@RequestBody CommentDto commentDto) {
                commentService.updateComment(commentDto);
                return ResponseEntity.ok("Success, Your Comment was successfully updated");
        }

        @DeleteMapping("/delete-user-comment")
        public ResponseEntity<?> deleteUserComment(@RequestParam Long id) {
                commentService.deleteComment(id);
                return ResponseEntity.ok("Success, Your comment was successfully deleted");
        }

}
