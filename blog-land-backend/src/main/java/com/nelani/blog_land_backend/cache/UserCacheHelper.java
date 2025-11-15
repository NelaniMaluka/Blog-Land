package com.nelani.blog_land_backend.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class UserCacheHelper {

    private final CacheManager cacheManager;

    public UserCacheHelper(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /** Evict the user data */
    public void evictUser(@NonNull String email) {
        Cache cache = cacheManager.getCache("user");
        if (cache != null) {
            cache.evict(email);
        }
    }

    /** Evict the public user data */
    public void evictPublicUser(@NonNull String nanoId) {
        Cache cache = cacheManager.getCache("publicUser");
        if (cache != null) {
            cache.evict(nanoId);
        }
    }

    /** Evict all caches related to a user */
    public void evictAllForUser(@NonNull String email, @NonNull String nanoId) {
        evictUser(email);
        evictPublicUser(nanoId);
    }
}
