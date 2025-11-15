package com.nelani.blog_land_backend.sockets;

import com.nelani.blog_land_backend.mapper.UserMapper;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.model.UserSocial;
import com.nelani.blog_land_backend.response.UserResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserSocket {

    private final SimpMessagingTemplate messagingTemplate;

    public UserSocket(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void updateUser(User user, List<UserSocial> socials) {
        // Map to DTO
        UserResponse userResponse = UserMapper.buildLoggedInUser(user, socials);

        // Send updated comment to subscribers
        messagingTemplate.convertAndSendToUser(
                user.getEmail(),
                "/queue/user/update",
                userResponse);
    }

    public void updatePublicUser(User user) {
        // Map to DTO
        UserResponse userResponse = UserMapper.publicUserWithMinimalDetails(user);

        // Send updated comment to subscribers
        messagingTemplate.convertAndSend(
                "/topic/user/update/" + user.getNaniId(),
                userResponse);
    }
}
