package com.nelani.blog_land_backend.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

        @Bean
        public CacheManager cacheManager() {
                CaffeineCacheManager cacheManager = new CaffeineCacheManager();

                // Comment-related caches (frequently updated)
                Caffeine<Object, Object> commentCache = Caffeine.newBuilder()
                                .expireAfterWrite(5, TimeUnit.MINUTES)
                                .maximumSize(500);

                var builtCommentCache = commentCache.build();
                if (builtCommentCache != null) {
                        cacheManager.registerCustomCache("postComments", builtCommentCache);
                        cacheManager.registerCustomCache("postCommentsCount", builtCommentCache);
                        cacheManager.registerCustomCache("postLikesCount", builtCommentCache);
                        cacheManager.registerCustomCache("userComments", builtCommentCache);
                        cacheManager.registerCustomCache("userLikes", builtCommentCache);
                }

                // User activity and trending-related caches (moderately dynamic)
                Caffeine<Object, Object> activityCache = Caffeine.newBuilder()
                                .expireAfterWrite(1, TimeUnit.HOURS)
                                .maximumSize(300);

                var builtActivityCache = activityCache.build();
                if (builtActivityCache != null) {
                        cacheManager.registerCustomCache("userPosts", builtActivityCache);
                        cacheManager.registerCustomCache("trendingPosts", builtActivityCache);
                        cacheManager.registerCustomCache("allPosts", builtActivityCache);
                }

                // Post and category data caches (mostly static)
                Caffeine<Object, Object> contentCache = Caffeine.newBuilder()
                                .expireAfterWrite(7, TimeUnit.DAYS)
                                .maximumSize(200);

                var builtContentCache = contentCache.build();
                if (builtContentCache != null) {
                        cacheManager.registerCustomCache("post", builtContentCache);
                        cacheManager.registerCustomCache("categoryPosts", builtContentCache);
                        cacheManager.registerCustomCache("categories", builtContentCache);
                        cacheManager.registerCustomCache("user", builtContentCache);
                }

                return cacheManager;
        }

}
