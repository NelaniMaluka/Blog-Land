package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.Sockets.CommentSocket;
import com.nelani.blog_land_backend.Util.Validation.*;
import com.nelani.blog_land_backend.Util.Builders.PostBuilder;
import com.nelani.blog_land_backend.dto.CommentDto;
import com.nelani.blog_land_backend.model.Comment;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.CommentRepository;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.response.CommentResponse;
import com.nelani.blog_land_backend.service.CommentService;

import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Comparator;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private final EntityManager entityManager;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final ModerationValidator moderationValidator;
    private final CommentSocket commentSocket;

    public CommentServiceImpl(EntityManager entityManager, CommentRepository commentRepository, PostRepository postRepository, ModerationValidator moderationValidator, CommentSocket commentSocket) {
        this.entityManager = entityManager;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.moderationValidator = moderationValidator;
        this.commentSocket = commentSocket;
    }

    @Override
    @Transactional
    public long getCountByPostId(Long postId) {
        // Validate fields
        FormValidation.assertRequiredField(postId, "Post Id");

        // Check if the post exists
        Optional<Post> post = postRepository.findById(postId);
        PostValidation.assertPostExists(post);

        return commentRepository.countByPost(post.get());
    }

    @Override
    @Transactional
    public Page<CommentResponse> getByPostId(Long postId, int page, int size) {
        // Validate fields
        Long id = FormValidation.assertRequiredField(postId, "Post Id");

        // Validate post existence
        Optional<Post> optionalPost = postRepository.findById(id);
        PostValidation.assertPostExists(optionalPost);

        // Fetch paginated comment by post
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findByPostId(id, pageable);

        // Sort comments before mapping
        List<CommentResponse> sorted = commentPage.getContent().stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed()) // newest first
                .map(PostBuilder::mapComment)
                .toList();

        // Re-wrap in a PageImpl to preserve pagination metadata
        return new PageImpl<>(sorted, commentPage.getPageable(), commentPage.getTotalElements());
    }

    @Override
    @Transactional
    public List<CommentResponse> getByUserId(long postId) {
        // Validate fields
        FormValidation.assertRequiredField(postId, "Post Id");

        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Fetch comments by user and post
        List<Comment> comments = commentRepository.findByUserIdAndPostId(user.getId(), postId);

        // Map to response DTO
        return comments.stream()
                .map(PostBuilder::mapCommentIds)
                .toList();
    }

    @Override
    @Transactional
    public void addComment(CommentDto commentDto) {
        // Validate fields
        Long postId = FormValidation.assertRequiredField(commentDto.getPostId(), "Post Id");
        String content = FormValidation.assertRequiredField(commentDto.getContent(), "Content");

        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Checks if the post exists
        Optional<Post> post = postRepository.findById(postId);
        Post existingPost = PostValidation.assertPostExist(post);

        // Build new post
        Comment newComment = Comment.builder()
                .content(content)
                .user(user)
                .post(existingPost)
                .build();

        // Moderate content
        moderationValidator.commentModeration(newComment);

        commentRepository.save(newComment); // Save the new comment

        // Update the socket
        commentSocket.updateCommentCount(post.get());
        commentSocket.addNewComments(post.get(), newComment);
    };

    @Override
    @Transactional
    public void updateComment(CommentDto commentDto) {
        // Validate fields
        Long commentId = FormValidation.assertRequiredField(commentDto.getId(), "Comment Id");
        String content = FormValidation.assertRequiredField(commentDto.getContent(), "Content");
        Long postId = FormValidation.assertRequiredField(commentDto.getPostId(), "Post Id");

        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Checks if the post exists
        Optional<Post> post = postRepository.findById(postId);
        PostValidation.assertPostExists(post);

        // Checks if the Comment exists
        Optional<Comment> comment = commentRepository.findById(commentId);
        Comment existingComment = CommentValidation.assertCommentExist(comment);

        // Checks if the comment belongs to the user
        CommentValidation.assertCommentBelongsToUser(existingComment, user);

        // Update existing comment
        existingComment.setContent(content);

        // Moderate content
        moderationValidator.commentModeration(existingComment);

        commentRepository.save(existingComment); // Save comment

        // Update the socket
        commentSocket.updateComment(post.get(), existingComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        // Validate fields
        Long commentId = FormValidation.assertRequiredField(id, "Comment Id");

        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Get the comment
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Check ownership
        CommentValidation.assertCommentBelongsToUser(comment, user);

        // Remove from user collection (triggers orphanRemoval if configured)
        comment.getUser().getComments().remove(comment);

        // Delete the comment
        commentRepository.delete(comment);

        // Optionally flush to commit immediately
        entityManager.flush();

        // Update sockets
        commentSocket.updateCommentCount(comment.getPost());
        commentSocket.deleteComment(comment.getPost(), commentId);
    }
}
