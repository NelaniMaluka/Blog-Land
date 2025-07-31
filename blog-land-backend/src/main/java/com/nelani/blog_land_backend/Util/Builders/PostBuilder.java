package com.nelani.blog_land_backend.Util.Builders;

import com.nelani.blog_land_backend.model.Comment;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.response.CommentResponse;
import com.nelani.blog_land_backend.response.PostResponse;

import java.util.List;

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
        response.setUser(UserBuilder.publicUser(post.getUser()));
        response.setComments(mapComments(post.getComments()));
        return response;
    }

    public static PostResponse generatePost(Post post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setReadTime(post.getReadTime());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        response.setPostImgUrl(post.getImgUrl());
        response.setViews(post.getViewCount());
        response.setSummary(post.getSummary());
        response.setCommentCount(post.getComments() != null ? post.getComments().size() : 0);
        response.setUser(UserBuilder.publicUserWithMinimalDetails(post.getUser()));
        return response;
    }

    public static List<CommentResponse> mapComments(List<Comment> comments) {
        return comments.stream()
                .map(c -> new CommentResponse(
                        c.getId(),
                        c.getPost().getId(),
                        c.getContent(),
                        c.getUser().getFirstname() + " " + c.getUser().getLastname(),
                        c.getUser().getProfileIconUrl(),
                        c.getCreatedAt()))
                .toList();
    }

    public static CommentResponse mapComment(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setPostId(comment.getPost().getId());
        response.setContent(comment.getContent());
        response.setAuthor(comment.getUser().getFirstname() + " " + comment.getUser().getLastname());
        response.setProfileImgUrl(comment.getUser().getProfileIconUrl());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }

    public static int calculateReadTime(String content) {
        int words = content == null ? 0 : content.trim().split("\\s+").length;
        int averageWordsPerMinute = 200;
        return Math.max(1, words / averageWordsPerMinute);
    }

}
