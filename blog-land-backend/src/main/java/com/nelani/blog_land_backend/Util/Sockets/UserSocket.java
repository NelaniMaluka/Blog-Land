package com.nelani.blog_land_backend.Util.Sockets;

import com.nelani.blog_land_backend.Util.Builders.PostBuilder;
import com.nelani.blog_land_backend.Util.Builders.UserBuilder;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.response.PostResponse;
import com.nelani.blog_land_backend.response.UserResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserSocket {

    private final SimpMessagingTemplate messagingTemplate;

    public UserSocket(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void updateUser(User user) {
        // Map to DTO
        UserResponse userResponse = UserBuilder.buildLoggedInUser(user);

        // Send updated comment to subscribers
        messagingTemplate.convertAndSendToUser(
                user.getId().toString(),
                "/queue/user/update",
                userResponse
        );
    }
}
