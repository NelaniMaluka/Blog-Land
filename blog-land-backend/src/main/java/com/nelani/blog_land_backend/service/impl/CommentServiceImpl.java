package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.FormValidation;
import com.nelani.blog_land_backend.Util.PostBuilder;
import com.nelani.blog_land_backend.Util.ResponseBuilder;
import com.nelani.blog_land_backend.Util.UserValidation;
import com.nelani.blog_land_backend.dto.CommentDto;
import com.nelani.blog_land_backend.model.Comment;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.CommentRepository;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.response.CommentResponse;
import com.nelani.blog_land_backend.service.CommentService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    @PersistenceContext
    private EntityManager entityManager;

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository,
            UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<?> getByPostId(Long postId, int page, int size) {
        // Trimand validate
        Long id = FormValidation.trimAndValidate(postId, "Post Id");

        // Validate post existence
        Optional<Post> optionalCategory = postRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            return ResponseBuilder.invalid("Validation Error",
                    "Post with ID " + id + " does not exist.");
        }

        Pageable pageable = PageRequest.of(page, size);

        // Fetch paginated comment by post
        Page<Comment> postPage = commentRepository.findByPostId(id, pageable);

        // Convert to CommentResponse while retaining pagination metadata
        Page<CommentResponse> responsePage = postPage.map(PostBuilder::mapComment);

        return ResponseEntity.ok(responsePage);
    }

    @Override
    @Transactional
    public ResponseEntity<?> getByUserId(Long userId, int page, int size) {
        // Trim and validate
        Long id = FormValidation.trimAndValidate(userId, "User Id");

        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Checks if the token and user id match
        if (!user.getId().equals(userId)) {
            return ResponseBuilder.invalid("Authorization Error",
                    "The user ID provided does not match the authenticated account. Please verify your credentials.");
        }

        // Checks if users have posts
        if (user.getComments().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        Pageable pageable = PageRequest.of(page, size);

        // Fetch paginated comment by post
        Page<Comment> postPage = commentRepository.findByUserId(id, pageable);

        // Convert to CommentResponse while retaining pagination metadata
        Page<CommentResponse> responsePage = postPage.map(PostBuilder::mapComment);

        return ResponseEntity.ok(responsePage);
    }

    @Override
    @Transactional
    public ResponseEntity<?> addComment(CommentDto commentDto) {
        // Trim and validate
        String content = FormValidation.trimAndValidate(commentDto.getContent(), "Comment Id");
        Long userId = FormValidation.trimAndValidate(commentDto.getUserId(), "User ID");
        Long postId = FormValidation.trimAndValidate(commentDto.getPostId(), "Category Id");

        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Checks if the token and user id match
        if (!user.getId().equals(userId)) {
            return ResponseBuilder.invalid("Authorization Error",
                    "The user ID provided does not match the authenticated account. Please verify your credentials.");
        }

        // Checks if the comment exists
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            return ResponseBuilder.invalid("validation.error", "post.notFound");
        }

        // Build new post
        Comment newComment = Comment.builder()
                .content(content)
                .user(user)
                .post(post.get())
                .build();

        user.getComments().add(newComment);
        userRepository.save(user);

        return ResponseEntity.ok("Success, Your comment was successfully added");
    };

    @Override
    @Transactional
    public ResponseEntity<?> updateComment(CommentDto commentDto) {
        // Trim and validate
        Long id = FormValidation.trimAndValidate(commentDto.getId(), "Post Id");
        String content = FormValidation.trimAndValidate(commentDto.getContent(), "Comment Id");
        Long userId = FormValidation.trimAndValidate(commentDto.getUserId(), "User ID");
        Long postId = FormValidation.trimAndValidate(commentDto.getPostId(), "Category Id");

        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Checks if the token and user id match
        if (!user.getId().equals(userId)) {
            return ResponseBuilder.invalid("Authorization Error",
                    "The user ID provided does not match the authenticated account. Please verify your credentials.");
        }

        // Checks if the post exists
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            return ResponseBuilder.invalid("validation.error", "post.notFound");
        }

        // Checks if the Comment exists
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isEmpty()) {
            return ResponseBuilder.invalid("Comment not found",
                    "No post with ID " + id + " exists.");
        }

        // Checks if the comment belongs to the user
        if (!comment.get().getUser().getId().equals(userId)) {
            return ResponseBuilder.unauthorized("Authorization Error",
                    "The user ID provided does not match the author's id . Please verify your credentials.");
        }

        // Build new comment
        Comment updatedComment = comment.get();
        updatedComment.setContent(content);
        updatedComment.setUser(user);
        updatedComment.setPost(post.get());

        commentRepository.save(updatedComment);

        return ResponseEntity.ok("Success, Your Comment was successfully updated");
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteComment(CommentDto commentDto) {
        // Trim and validate
        Long id = FormValidation.trimAndValidate(commentDto.getId(), "Comment Id");
        Long userId = FormValidation.trimAndValidate(commentDto.getUserId(), "User Id");

        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Checks if the token and user id match
        if (!user.getId().equals(userId)) {
            return ResponseBuilder.invalid("Authorization Error",
                    "The user ID provided does not match the authenticated account. Please verify your credentials.");
        }

        // Checks if the comment exists
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isEmpty()) {
            return ResponseBuilder.invalid("Comment not found",
                    "No comment with ID " + id + " exists.");
        }

        // Checks if the post belongs to the user
        if (!comment.get().getUser().getId().equals(userId)) {
            return ResponseBuilder.unauthorized("Authorization Error",
                    "The user ID provided does not match the author's id . Please verify your credentials.");
        }

        Comment existingComment = comment.get();

        try {
            // Ensure the comment is managed
            Comment managedComment = entityManager.contains(existingComment)
                    ? existingComment
                    : entityManager.merge(existingComment);

            // Let cascade and orphanRemoval do the cleanup
            entityManager.remove(managedComment);

            // Flush to trigger SQL operations
            entityManager.flush();
            entityManager.clear();

        } catch (Exception e) {
            throw new RuntimeException("Comment deletion failed.");
        }

        return ResponseEntity.ok("Success, Your comment was successfully deleted");
    }
}
