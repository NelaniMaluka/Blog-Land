package com.nelani.blog_land_backend.util.caches;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class LikeCacheHelper {

    private final CacheManager cacheManager;

    public LikeCacheHelper(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /** Evict the count of likes for a post */
    public void evictPostLikesCount(Long postId) {
        Cache cache = cacheManager.getCache("postLikesCount");
        if (cache != null) {
            cache.evict(postId);
        }
    }

    /** Evict the list of the user’s likes */
    public void evictUserLikes(Long userId, Long postId) {
        Cache cache = cacheManager.getCache("userLikes");
        if (cache != null) {
            cache.evict(userId);
        }
    }

    /** Evict all caches related to a post for a specific user */
    public void evictAllForPost(Long userId, Long postId) {
        evictPostLikesCount(postId);
        evictUserLikes(userId, postId);
    }
}
