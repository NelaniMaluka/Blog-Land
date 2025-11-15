package com.nelani.blog_land_backend.sockets;

import com.nelani.blog_land_backend.mapper.PostBuilder;
import com.nelani.blog_land_backend.model.Comment;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.CommentRepository;
import com.nelani.blog_land_backend.response.CommentResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CommentSocket {

    private final CommentRepository commentRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public CommentSocket(CommentRepository commentRepository, SimpMessagingTemplate messagingTemplate) {
        this.commentRepository = commentRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public void updateCommentCount(Post post) {
        long count = commentRepository.countByPost(post);
        messagingTemplate.convertAndSend("/topic/posts/comments/count/" + post.getId(), count);
    }

    public void addNewComments(Post post, Comment newComment) {
        // Map to DTO
        CommentResponse commentResponse = PostBuilder.mapComment(newComment);

        // Send new comment to subscribers
        messagingTemplate.convertAndSend(
                "/topic/posts/comments/add/" + post.getId(),
                commentResponse);
    }

    public void updateComment(Post post, Comment updatedComment) {
        // Map to DTO
        CommentResponse commentResponse = PostBuilder.mapComment(updatedComment);

        // Send updated comment to subscribers
        messagingTemplate.convertAndSend(
                "/topic/posts/comments/update/" + post.getId(),
                commentResponse);
    }

    public void deleteComment(Post post, UUID deletedCommentId) {
        // Send deleted comment ID to subscribers
        messagingTemplate.convertAndSend(
                "/topic/posts/comments/remove/" + post.getId(),
                deletedCommentId);
    }

    public void addUserComment(User user, Comment comment, Post post) {
        // Send updated comment to subscribers
        messagingTemplate.convertAndSendToUser(
                user.getEmail(),
                "/queue/posts/comment/add/" + post.getId(),
                comment.getId());
    }

    public void removeUserComment(User user, Comment comment, Post post) {
        // Send updated comment to subscribers
        messagingTemplate.convertAndSendToUser(
                user.getEmail(),
                "/queue/posts/comment/remove/" + post.getId(),
                comment.getId());
    }
}
