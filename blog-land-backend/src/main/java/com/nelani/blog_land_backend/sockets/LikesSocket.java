package com.nelani.blog_land_backend.sockets;

import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.response.LikeResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class LikesSocket {

    private final SimpMessagingTemplate messagingTemplate;

    public LikesSocket(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void updatePostLikes(long count, UUID postId) {
        messagingTemplate.convertAndSend("/topic/posts/likes/" + postId, count);
    }

    public void updateUserLikes(User user, List<LikeResponse> likes) {
        messagingTemplate.convertAndSendToUser(
                user.getEmail(),
                "/queue/user/posts/likes/update",
                likes);
    }
}
