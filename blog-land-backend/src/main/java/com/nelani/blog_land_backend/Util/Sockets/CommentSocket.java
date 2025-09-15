package com.nelani.blog_land_backend.Util.Sockets;

import com.nelani.blog_land_backend.Util.Builders.PostBuilder;
import com.nelani.blog_land_backend.Util.Builders.UserBuilder;
import com.nelani.blog_land_backend.model.Comment;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.CommentRepository;
import com.nelani.blog_land_backend.response.CommentResponse;
import com.nelani.blog_land_backend.response.UserResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import static com.nelani.blog_land_backend.Util.Builders.PostBuilder.mapCommentIds;

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
        messagingTemplate.convertAndSend("/topic/comments/comments-count/" + post.getId(), count);
    }

    public void addNewComments(Post post, Comment newComment) {
        // Map to DTO
        CommentResponse commentResponse = PostBuilder.mapComment(newComment);

        // Send new comment to subscribers
        messagingTemplate.convertAndSend(
                "/topic/comments/add/" + post.getId(),
                commentResponse
        );
    }

    public void updateComment(Post post, Comment updatedComment) {
        // Map to DTO
        CommentResponse commentResponse = PostBuilder.mapComment(updatedComment);

        // Send updated comment to subscribers
        messagingTemplate.convertAndSend(
                "/topic/comments/update/" + post.getId(),
                commentResponse
        );
    }

    public void deleteComment(Post post, Long deletedCommentId) {
        // Send deleted comment ID to subscribers
        messagingTemplate.convertAndSend(
                "/topic/comments/remove/" + post.getId(),
                 deletedCommentId
        );
    }

    public void addUserComment(User user, Comment comment, Post post) {
        // Map to DTO
        CommentResponse newCommentId = mapCommentIds(comment);

        // Send updated comment to subscribers
        messagingTemplate.convertAndSendToUser(
                user.getId().toString(),
                "/queue/comment/add/" + post.getId(),
                newCommentId);
    }

    public void removeUserComment(User user, Comment comment, Post post) {
        // Send updated comment to subscribers
        messagingTemplate.convertAndSendToUser(
                user.getId().toString(),
                "/queue/comment/remove/" + post.getId(),
               comment.getId());
    }
}
