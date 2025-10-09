package com.nelani.blog_land_backend.sockets;

import com.nelani.blog_land_backend.util.builders.UserBuilder;
import com.nelani.blog_land_backend.model.User;
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
                userResponse);
    }

    public void updatePublicUser(User user) {
        // Map to DTO
        UserResponse userResponse = UserBuilder.publicUserWithMinimalDetails(user);

        // Send updated comment to subscribers
        messagingTemplate.convertAndSend(
                "/queue/user/public-update/"  + user.getNaniId(),
                userResponse
        );
    }
}
