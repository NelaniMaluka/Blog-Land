package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.cache.CommentCacheHelper;
import com.nelani.blog_land_backend.sockets.CommentSocket;
import com.nelani.blog_land_backend.util.validation.*;
import com.nelani.blog_land_backend.mapper.PostBuilder;
import com.nelani.blog_land_backend.dto.CommentDto;
import com.nelani.blog_land_backend.model.Comment;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.CommentRepository;
import com.nelani.blog_land_backend.response.CommentResponse;
import com.nelani.blog_land_backend.service.CommentService;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Comparator;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {

        private final CommentCacheHelper commentCacheHelper;
        private final CommentRepository commentRepository;
        private final ModerationValidator moderationValidator;
        private final CommentSocket commentSocket;
        private final PostRepository postRepository;
        private final UserValidation userValidation;

        public CommentServiceImpl(CommentCacheHelper commentCacheHelper, CommentRepository commentRepository,
                        ModerationValidator moderationValidator, CommentSocket commentSocket,
                        PostRepository postRepository, UserValidation userValidation) {
                this.commentCacheHelper = commentCacheHelper;
                this.commentRepository = commentRepository;
                this.moderationValidator = moderationValidator;
                this.commentSocket = commentSocket;
                this.postRepository = postRepository;
                this.userValidation = userValidation;
        }

        @Override
        @Transactional
        @Cacheable(value = "postCommentsCount", key = "#postId")
        public long getCountByPostId(UUID postId) {
                // Check if the post exists
                Post post = postRepository.findById(postId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Post not found."));

                return commentRepository.countByPost(post);
        }

        @Override
        @Transactional
        @Cacheable(value = "postComments", key = "#postId + '_' + #page + '_' + #size")
        public Page<CommentResponse> getByPostId(UUID postId, int page, int size) {
                // Validate post existence
                postRepository.findById(postId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Post not found."));

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
        @Cacheable(value = "userComments", key = "T(org.springframework.security.core.context.SecurityContextHolder).context.authentication.name + '_' + #postId")
        public List<UUID> getByUserId(UUID postId) {
                // Get current authenticated user
                User user = userValidation.getAuthenticatedUser();

                // Fetch comments by user and post
                List<Comment> comments = commentRepository.findByUserIdAndPostId(user.getId(), postId);

                // Map to response DTO
                return comments.stream()
                                .map(Comment::getId)
                                .toList();
        }

        @Override
        @Transactional
        public void addComment(UUID postId, CommentDto commentDto) {
                // Get current authenticated user
                User user = userValidation.getAuthenticatedUser();

                // Checks if the post exists
                Post existingPost = postRepository.findById(postId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Post not found."));

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

                commentCacheHelper.evictAllForPost(user.getId(), postId); // Evict Caches
        }

        @Override
        @Transactional
        public void updateComment(UUID postId, UUID commentId, CommentDto commentDto) {
                // Get current authenticated user
                User user = userValidation.getAuthenticatedUser();

                // Checks if the post exists
                Post post = postRepository.findById(postId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Post not found."));

                // Checks if the Comment exists
                Comment existingComment = commentRepository.findById(commentId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Comment not found."));

                // Check ownership
                if (!existingComment.getUser().getId().equals(user.getId())) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                                        "You are not authorized to perform this action on this comment.");
                }

                // Update existing comment
                existingComment.setContent(commentDto.content());

                // Moderate content
                moderationValidator.commentModeration(existingComment);

                commentRepository.save(existingComment); // Save comment

                // Update the socket
                commentSocket.updateComment(post, existingComment);

                // Evict Caches
                commentCacheHelper.evictPostCommentsPaginated(postId);
        }

        @Override
        @Transactional
        public void deleteComment(UUID commentId) {
                // Get current authenticated user
                User user = userValidation.getAuthenticatedUser();

                // Get the comment
                Comment comment = commentRepository.findById(commentId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Comment not found"));

                // Check ownership
                if (!comment.getUser().getId().equals(user.getId())) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                                        "You are not authorized to perform this action on this comment.");
                }

                // Delete the comment
                commentRepository.delete(comment);

                // Update sockets
                commentSocket.updateCommentCount(comment.getPost());
                commentSocket.deleteComment(comment.getPost(), commentId);
                commentSocket.removeUserComment(user, comment, comment.getPost());

                commentCacheHelper.evictAllForPost(user.getId(), comment.getPost().getId()); // Evict Caches
        }

}
