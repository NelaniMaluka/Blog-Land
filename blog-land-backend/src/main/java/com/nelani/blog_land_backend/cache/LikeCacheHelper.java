package com.nelani.blog_land_backend.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LikeCacheHelper {

    private final CacheManager cacheManager;

    public LikeCacheHelper(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /** Evict the count of likes for a post */
    public void evictPostLikesCount(@NonNull UUID postId) {
        Cache cache = cacheManager.getCache("postLikesCount");
        if (cache != null) {
            cache.evict("likes-" + postId);
        }
    }

    /** Evict the list of the user’s likes */
    public void evictUserLikes(@NonNull String email) {
        Cache cache = cacheManager.getCache("userLikes");
        if (cache != null) {
            cache.evict(email);
        }
    }

    /** Evict all caches related to a post for a specific user */
    public void evictAllForPost(@NonNull String email, @NonNull UUID postId) {
        evictPostLikesCount(postId);
        evictUserLikes(email);
    }
}
