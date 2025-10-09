package com.nelani.blog_land_backend.util.builders;

import com.nelani.blog_land_backend.model.Comment;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.response.CommentResponse;
import com.nelani.blog_land_backend.response.PostResponse;

public class PostBuilder {

    public static PostResponse generateUserPostWithUserInfo(Post post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setReadTime(post.getReadTime());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        response.setCategoryId(post.getCategory().getId());
        response.setPostImgUrl(post.getImgUrl());
        response.setViews(post.getViewCount());
        response.setSummary(post.getSummary());
        response.setReferences(post.getReferences());
        response.setUserId(post.getUser().getNaniId());
        return response;
    }

    public static PostResponse generatePost(Post post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setReadTime(post.getReadTime());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        response.setCategoryId(post.getCategory().getId());
        response.setPostImgUrl(post.getImgUrl());
        response.setViews(post.getViewCount());
        response.setSummary(post.getSummary());
        response.setUserId(post.getUser().getNaniId());
        response.setDraft(post.isDraft());
        return response;
    }

    public static CommentResponse mapComment(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUserId(comment.getUser().getNaniId());
        return response;
    }

    public static CommentResponse mapCommentIds(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        return response;
    }

    public static int calculateReadTime(String content) {
        int words = content == null ? 0 : content.trim().split("\\s+").length;
        int averageWordsPerMinute = 200;
        return Math.max(1, words / averageWordsPerMinute);
    }

}
