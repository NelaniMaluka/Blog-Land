package com.nelani.blog_land_backend.sockets;

import com.nelani.blog_land_backend.mapper.PostBuilder;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.response.PostResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PostSocket {

    private final SimpMessagingTemplate messagingTemplate;

    public PostSocket(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void addNewPost(Post post) {
        // Map to DTO
        PostResponse postResponse = PostBuilder.generateUserPostWithUserInfo(post);

        // Send new post to subscribers
        messagingTemplate.convertAndSend(
                "/topic/posts/add/" + post.getId(),
                postResponse);
    }

    public void updatePost(Post post) {
        // Map to DTO
        PostResponse postResponse = PostBuilder.generateUserPostWithUserInfo(post);

        // Send updated comment to subscribers
        messagingTemplate.convertAndSend(
                "/topic/posts/update/" + post.getId(),
                postResponse);
    }

    public void deletePost(UUID deletedPostId) {
        // Send deleted post ID to subscribers
        messagingTemplate.convertAndSend(
                "/topic/posts/remove/" + deletedPostId,
                deletedPostId);
    }

}
