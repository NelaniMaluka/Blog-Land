package com.nelani.blog_land_backend.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PostCacheHelper {

    private final CacheManager cacheManager;

    public PostCacheHelper(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /** Evict the post */
    public void evictPost(UUID postId) {
        Cache cache = cacheManager.getCache("post");
        if (cache != null) {
            cache.evict(postId);
        }
    }

    /** Evict the entire trending posts cache */
    public void evictTrendingPosts() {
        Cache cache = cacheManager.getCache("trendingPosts");
        if (cache != null) {
            cache.clear();
        }
    }

    /** Evict the all posts cache */
    public void evictAllPosts() {
        Cache cache = cacheManager.getCache("allPosts");
        if (cache != null) {
            cache.clear();
        }
    }

    /** Evict all paginated user posts */
    public void evictUserPosts(String email) {
        Cache cache = cacheManager.getCache("userPosts");
        if (cache != null) {
            String prefix = email + "_";
            // Extract keys from the cache using Caffeine API
            Object nativeCache = cache.getNativeCache();
            if (nativeCache instanceof com.github.benmanes.caffeine.cache.Cache<?, ?> caffeineCache) {
                caffeineCache.asMap().keySet().stream()
                        .filter(key -> key.toString().startsWith(prefix))
                        .forEach(cache::evict); // Spring-safe eviction
            }
        }
    }

    /** Evict all paginated category posts */
    public void evictCategoryPosts(UUID categoryId) {
        Cache cache = cacheManager.getCache("categoryPosts");
        if (cache != null) {
            String prefix = categoryId + "_";
            Object nativeCache = cache.getNativeCache();
            if (nativeCache instanceof com.github.benmanes.caffeine.cache.Cache<?, ?> caffeineCache) {
                caffeineCache.asMap().keySet().stream()
                        .filter(key -> key.toString().startsWith(prefix))
                        .forEach(cache::evict);
            }
        }
    }

    /** Evict all caches related to a user */
    public void evictAllUserPosts(String email, UUID postId, UUID categoryId) {
        evictPost(postId);
        evictTrendingPosts();
        evictAllPosts();
        evictCategoryPosts(categoryId);
        evictUserPosts(email);
    }

}
