package com.nelani.blog_land_backend.mapper;

import com.nelani.blog_land_backend.model.Comment;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.response.CommentResponse;
import com.nelani.blog_land_backend.response.PostResponse;

public class PostBuilder {

    public static PostResponse generateUserPostWithUserInfo(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .readTime(post.getReadTime())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .categoryId(post.getCategory().getId())
                .postImgUrl(post.getImgUrl())
                .views(post.getViewCount())
                .summary(post.getSummary())
                .references(post.getReferences())
                .userId(post.getUser().getNaniId())
                .build();
    }

    public static PostResponse generatePost(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .readTime(post.getReadTime())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .categoryId(post.getCategory().getId())
                .postImgUrl(post.getImgUrl())
                .views(post.getViewCount())
                .summary(post.getSummary())
                .userId(post.getUser().getNaniId())
                .isDraft(post.isDraft())
                .build();
    }

    public static CommentResponse mapComment(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .userId(comment.getUser().getNaniId())
                .build();
    }

    public static int calculateReadTime(String content) {
        int words = content == null ? 0 : content.trim().split("\\s+").length;
        int averageWordsPerMinute = 200;
        return Math.max(1, words / averageWordsPerMinute);
    }

}
