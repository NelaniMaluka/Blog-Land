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
    public void evictUserComments(UUID userId, UUID postId) {
        Cache cache = cacheManager.getCache("userComments");
        if (cache != null) {
            String key = userId + "_" + postId;
            cache.evict(key); // keys are Strings
        }
    }

    /** Evict all paginated comments for a post */
    public void evictPostCommentsPaginated(UUID postId) {
        Cache cache = cacheManager.getCache("postComments");
        if (cache != null) {
            cache.clear(); // safest simple option
        }
    }

    /** Evict all caches related to a post for a specific user */
    public void evictAllForPost(UUID userId, UUID postId) {
        evictPostCommentsCount(postId);
        evictUserComments(userId, postId);
        evictPostCommentsPaginated(postId);
    }
}
