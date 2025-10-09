package com.nelani.blog_land_backend.schedule;

import com.nelani.blog_land_backend.service.PostService;

import lombok.extern.log4j.Log4j2;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
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
            log.info("Cache 'latestPosts' cleared at midnight");

            // repopulate for first 5 pages
            for (int page = 0; page < 5; page++) {
                postService.getLatestPost(page, 20); // triggers caching
                log.info("Repopulated cache for page {}", page);
            }
        } else {
            log.warn("Cache 'latestPosts' not found!");
        }
    }
}
