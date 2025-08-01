package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.Util.Builders.PostBuilder;
import com.nelani.blog_land_backend.dto.CommentDto;
import com.nelani.blog_land_backend.model.Comment;
import com.nelani.blog_land_backend.repository.CommentRepository;
import com.nelani.blog_land_backend.response.CommentResponse;
import com.nelani.blog_land_backend.service.CommentService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentRepository commentRepository;
    private final CommentService commentService;

    public CommentController(CommentRepository commentRepository, CommentService commentService) {
        this.commentRepository = commentRepository;
        this.commentService = commentService;
    }

    @GetMapping("/get/comments")
    public ResponseEntity<?> getAllComments(@RequestParam int page, @RequestParam int size) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Comment> postPage = commentRepository.findAll(pageable);
            Page<CommentResponse> responsePage = postPage.map(PostBuilder::mapComment);
            return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/get/category")
    public ResponseEntity<?> getAllCommentsByCategory(@RequestParam Long postId, @RequestParam int page,
            @RequestParam int size) {
            Page<CommentResponse> responsePage = commentService.getByPostId(postId, page, size);
            return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/get-user-comments")
    public ResponseEntity<?> getAllCommentsByUserId( @RequestParam int page,
            @RequestParam int size) {
            Page<CommentResponse> responsePage = commentService.getByUserId( page, size);
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
    public ResponseEntity<?> deleteUserComment(@RequestBody CommentDto commentDto) {
            commentService.deleteComment(commentDto);
            return ResponseEntity.ok("Success, Your comment was successfully deleted");
    }

}
