package com.nelani.blog_land_backend.Util.Sockets;

import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.repository.LikeRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikesSocket {

    private final SimpMessagingTemplate messagingTemplate;

    public LikesSocket(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void updatePostLikes(LikeRepository likeRepository, Post post) {
        long count = likeRepository.countByPost(post);
        messagingTemplate.convertAndSend("/topic/like/post-likes/" + post.getId(), count);
    }
}
