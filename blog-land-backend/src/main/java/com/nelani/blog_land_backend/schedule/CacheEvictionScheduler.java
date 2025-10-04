package com.nelani.blog_land_backend.schedule;

import com.nelani.blog_land_backend.service.PostService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheEvictionScheduler {

    private final CacheManager cacheManager;
    private final PostService postService;

    public CacheEvictionScheduler(CacheManager cacheManager, PostService postService) {
        this.cacheManager = cacheManager;
        this.postService = postService;
    }

    // Runs at midnight every day
    @Scheduled(cron = "0 0 0 * * ?")
    public void evictAndRepopulateCache() {
        Cache cache = cacheManager.getCache("latestPosts");
        if (cache != null) {
            cache.clear();
            System.out.println("Cache cleared at midnight");

            // repopulate for first 5 pages
            for (int page = 0; page < 5; page++) {
                postService.getLatestPost(page, 20); // triggers caching
                System.out.println("Repopulated cache for page " + page);
            }
        }
    }
}
