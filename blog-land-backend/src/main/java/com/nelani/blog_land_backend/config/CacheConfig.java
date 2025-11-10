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

                cacheManager.registerCustomCache("postComments", commentCache.build());
                cacheManager.registerCustomCache("postCommentsCount", commentCache.build());
                cacheManager.registerCustomCache("postLikesCount", commentCache.build());
                cacheManager.registerCustomCache("userComments", commentCache.build());
                cacheManager.registerCustomCache("userLikes", commentCache.build());

                // User activity and trending-related caches (moderately dynamic)
                Caffeine<Object, Object> activityCache = Caffeine.newBuilder()
                                .expireAfterWrite(1, TimeUnit.HOURS)
                                .maximumSize(300);

                cacheManager.registerCustomCache("userPosts", activityCache.build());
                cacheManager.registerCustomCache("trendingPosts", activityCache.build());
                cacheManager.registerCustomCache("allPosts", activityCache.build());

                // Post and category data caches (mostly static)
                Caffeine<Object, Object> contentCache = Caffeine.newBuilder()
                                .expireAfterWrite(7, TimeUnit.DAYS)
                                .maximumSize(200);

                cacheManager.registerCustomCache("post", contentCache.build());
                cacheManager.registerCustomCache("categoryPosts", contentCache.build());
                cacheManager.registerCustomCache("categories", contentCache.build());
                cacheManager.registerCustomCache("user", contentCache.build());

                return cacheManager;
        }
}
