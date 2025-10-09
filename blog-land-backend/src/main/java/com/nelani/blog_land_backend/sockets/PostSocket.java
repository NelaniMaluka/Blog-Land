package com.nelani.blog_land_backend.sockets;

import com.nelani.blog_land_backend.util.builders.PostBuilder;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.response.PostResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

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
                "/topic/post/add/" + post.getId(),
                postResponse);
    }

    public void updatePost(Post post) {
        // Map to DTO
        PostResponse postResponse = PostBuilder.generateUserPostWithUserInfo(post);

        // Send updated comment to subscribers
        messagingTemplate.convertAndSend(
                "/topic/post/update/" + post.getId(),
                postResponse);
    }

    public void deletePost(Long deletedPostId) {
        // Send deleted post ID to subscribers
        messagingTemplate.convertAndSend(
                "/topic/post/remove/" + deletedPostId,
                deletedPostId);
    }

}
