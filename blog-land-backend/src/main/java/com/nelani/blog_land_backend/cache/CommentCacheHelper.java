package com.nelani.blog_land_backend.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CommentCacheHelper {

    private final CacheManager cacheManager;

    public CommentCacheHelper(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /** Evict the count of comments for a post */
    public void evictPostCommentsCount(UUID postId) {
        Cache cache = cacheManager.getCache("postCommentsCount");
        if (cache != null)
            cache.evict(postId);
    }

    /** Evict the user’s comments for a post */
    public void evictUserComments(String email, UUID postId) {
        Cache cache = cacheManager.getCache("userComments");
        if (cache != null) {
            String key = email + "_" + postId;
            cache.evict(key); // keys are Strings
        }
    }

    /** Evict all paginated comments for a post */
    public void evictPostCommentsPaginated() {
        Cache cache = cacheManager.getCache("postComments");
        if (cache != null) {
            cache.clear(); // safest simple option
        }
    }

    /** Evict all caches related to a post for a specific user */
    public void evictAllForPost(String email, UUID postId) {
        evictPostCommentsCount(postId);
        evictUserComments(email, postId);
        evictPostCommentsPaginated();
    }
}
