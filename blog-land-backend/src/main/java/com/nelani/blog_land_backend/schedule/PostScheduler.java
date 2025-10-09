package com.nelani.blog_land_backend.schedule;

import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.repository.PostRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
public class PostScheduler {

    private final PostRepository postRepository;

    public PostScheduler(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // Runs every minute
    @Scheduled(fixedRate = 60000)
    public void publishScheduledPosts() {
        List<Post> scheduledPosts = postRepository.findPostsToPublish();

        if (scheduledPosts.isEmpty()) {
            return;
        }

        log.info("Publishing {} scheduled post(s)...", scheduledPosts.size());

        for (Post post : scheduledPosts) {
            try {
                post.setDraft(false); // Mark as published
                postRepository.save(post);
                log.info("Published post with ID: {}", post.getId());
            } catch (Exception ex) {
                log.error("Failed to publish post with ID: {}. Error: {}", post.getId(), ex.getMessage(), ex);
            }
        }
    }
}
