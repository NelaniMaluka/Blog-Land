package com.nelani.blog_land_backend.config;

import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.repository.PostRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostScheduler {

    private final PostRepository postRepository;

    public PostScheduler(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // Runs every minute
    @Scheduled(fixedRate = 60000)
    public void publishScheduledPosts() {
        List<Post> scheduledPosts = postRepository.findPostsToPublish();
        for (Post post : scheduledPosts) {
            post.setDraft(false); // Mark as published
            postRepository.save(post);
        }
    }
}

