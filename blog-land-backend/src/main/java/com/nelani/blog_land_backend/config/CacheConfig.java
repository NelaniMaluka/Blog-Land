package com.nelani.blog_land_backend.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    // @Bean
    public CacheManager CacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        Caffeine<Object, Object> commentCacheConfig = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(5, TimeUnit.MINUTES);

        cacheManager.registerCustomCache("postCommentsCount", commentCacheConfig.build());
        cacheManager.registerCustomCache("postComments", commentCacheConfig.build());
        cacheManager.registerCustomCache("postLikesCount", commentCacheConfig.build());

        Caffeine<Object, Object> userCacheConfig = Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(1, TimeUnit.HOURS);

        cacheManager.registerCustomCache("userComments", userCacheConfig.build());
        cacheManager.registerCustomCache("userLikes", userCacheConfig.build());
        cacheManager.registerCustomCache("user", userCacheConfig.build());
        cacheManager.registerCustomCache("userPosts", userCacheConfig.build());
        cacheManager.registerCustomCache("trendingPosts", userCacheConfig.build());
        cacheManager.registerCustomCache("allPosts", userCacheConfig.build());

        Caffeine<Object, Object> postCacheConfig = Caffeine.newBuilder()
                .maximumSize(2000)
                .expireAfterWrite(7, TimeUnit.DAYS);

        cacheManager.registerCustomCache("categoryPosts", postCacheConfig.build());
        cacheManager.registerCustomCache("post", postCacheConfig.build());
        cacheManager.registerCustomCache("categories", postCacheConfig.build());

        return cacheManager;
    }
}


