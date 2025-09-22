package com.nelani.blog_land_backend.Util.Caches;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class UserCacheHelper {

    private final CacheManager cacheManager;

    public UserCacheHelper(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /** Evict the user data */
    public void evictUser(Long userId) {
        Cache cache = cacheManager.getCache("user");
        if (cache != null) {
            cache.evict(userId);
        }
    }

    /** Evict the public user data */
    public void evictUser(String nanoId) {
        Cache cache = cacheManager.getCache("publicUser");
        if (cache != null) {
            cache.evict(nanoId);
        }
    }

    /** Evict all caches related to a user */
    public void evictAllForUser(Long userId, String nanoId) {
        evictUser(userId);
        evictUser(nanoId);
    }
}
