package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.util.caches.CommentCacheHelper;
import com.nelani.blog_land_backend.sockets.CommentSocket;
import com.nelani.blog_land_backend.util.validation.*;
import com.nelani.blog_land_backend.util.builders.PostBuilder;
import com.nelani.blog_land_backend.dto.CommentDto;
import com.nelani.blog_land_backend.model.Comment;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.CommentRepository;
import com.nelani.blog_land_backend.response.CommentResponse;
import com.nelani.blog_land_backend.service.CommentService;

import jakarta.persistence.EntityManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Comparator;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentCacheHelper commentCacheHelper;
    private final EntityManager entityManager;
    private final CommentRepository commentRepository;
    private final ModerationValidator moderationValidator;
    private final CommentSocket commentSocket;
    private final PostValidation postValidation;
    private final CommentValidation commentValidation;

    public CommentServiceImpl(CommentCacheHelper commentCacheHelper, EntityManager entityManager,
            CommentRepository commentRepository, ModerationValidator moderationValidator, CommentSocket commentSocket,
            PostValidation postValidation, CommentValidation commentValidation) {
        this.commentCacheHelper = commentCacheHelper;
        this.entityManager = entityManager;
        this.commentRepository = commentRepository;
        this.moderationValidator = moderationValidator;
        this.commentSocket = commentSocket;
        this.postValidation = postValidation;
        this.commentValidation = commentValidation;
    }

    @Override
    @Transactional
    @Cacheable(value = "postCommentsCount", key = "#postId")
    public long getCountByPostId(Long postId) {
        // Check if the post exists
        Post post = postValidation.assertPostExist(postId);

        return commentRepository.countByPost(post);
    }

    @Override
    @Transactional
    @Cacheable(value = "postComments", key = "#postId + '_' + #page + '_' + #size")
    public Page<CommentResponse> getByPostId(Long postId, int page, int size) {
        // Validate post existence
        postValidation.assertPostExist(postId);

        // Fetch paginated comment by post
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findByPostId(postId, pageable);

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
    @Cacheable(value = "userComments", key = "T(com.nelani.blog_land_backend.util.validation.UserValidation).getCurrentUserId() + '_' + #postId")
    public List<CommentResponse> getByUserId(long postId) {
        // Get current authenticated user
        User user = UserValidation.getAuthenticatedUser();

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
        // Get current authenticated user
        User user = UserValidation.getAuthenticatedUser();

        // Checks if the post exists
        Post existingPost = postValidation.assertPostExist(commentDto.postId());

        // Build new post
        Comment newComment = Comment.builder()
                .content(commentDto.content())
                .user(user)
                .post(existingPost)
                .build();

        // Moderate content
        moderationValidator.commentModeration(newComment);

        commentRepository.save(newComment); // Save the new comment

        // Update the socket
        commentSocket.updateCommentCount(existingPost);
        commentSocket.addNewComments(existingPost, newComment);
        commentSocket.addUserComment(user, newComment, existingPost);

        commentCacheHelper.evictAllForPost(user.getId(), commentDto.postId()); // Evict Caches
    }

    @Override
    @Transactional
    public void updateComment(CommentDto commentDto) {
        // Get current authenticated user
        User user = UserValidation.getAuthenticatedUser();

        // Checks if the post exists
        Post post = postValidation.assertPostExist(commentDto.postId());

        // Checks if the Comment exists
        Comment existingComment = commentValidation.assertCommentExist(commentDto.id());

        // Checks if the comment belongs to the user
        commentValidation.assertCommentBelongsToUser(existingComment, user);

        // Update existing comment
        existingComment.setContent(commentDto.content());

        // Moderate content
        moderationValidator.commentModeration(existingComment);

        commentRepository.save(existingComment); // Save comment

        // Update the socket
        commentSocket.updateComment(post, existingComment);

        // Evict Caches
        commentCacheHelper.evictPostCommentsPaginated(commentDto.postId());
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        // Get current authenticated user
        User user = UserValidation.getAuthenticatedUser();

        // Get the comment
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Check ownership
        commentValidation.assertCommentBelongsToUser(comment, user);

        // Remove from user collection (triggers orphanRemoval if configured)
        comment.getUser().getComments().remove(comment);

        // Delete the comment
        commentRepository.delete(comment);

        // Optionally flush to commit immediately
        entityManager.flush();

        // Update sockets
        commentSocket.updateCommentCount(comment.getPost());
        commentSocket.deleteComment(comment.getPost(), commentId);
        commentSocket.removeUserComment(user, comment, comment.getPost());

        commentCacheHelper.evictAllForPost(user.getId(), comment.getPost().getId()); // Evict Caches
    }

}
