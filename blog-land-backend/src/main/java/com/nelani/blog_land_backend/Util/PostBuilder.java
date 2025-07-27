package com.nelani.blog_land_backend.Util;

import com.nelani.blog_land_backend.model.Comment;
import com.nelani.blog_land_backend.model.Like;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.response.CommentResponse;
import com.nelani.blog_land_backend.response.LikeResponse;
import com.nelani.blog_land_backend.response.PostResponse;

import java.util.List;
import java.util.stream.Collectors;

public class PostBuilder {

        public static List<PostResponse> generateUserPosts(List<Post> posts) {
            return posts.stream()
                    .map(post -> {
                        PostResponse response = new PostResponse();
                        response.setId(post.getId());
                        response.setTitle(post.getTitle());
                        response.setContent(post.getContent());
                        response.setReadTime(post.getReadTime());
                        response.setCreatedAt(post.getCreatedAt());
                        response.setUpdatedAt(post.getUpdatedAt());
                        response.setCategoryId(post.getCategory().getId());
                        response.setImgUrl(post.getImgUrl());
                        response.setSummary(post.getSummary());
                        response.setReferences(post.getReferences());
                        response.setComments(mapComments(post.getComments()));
                        response.setLikes(mapLikes(post.getLikes()));
                        return response;
                    })
                    .collect(Collectors.toList());
        }

    public static List<PostResponse> generateUserPostsWithUserInfo(List<Post> posts) {
        return posts.stream()
                .map(post -> {
                    PostResponse response = new PostResponse();
                    response.setId(post.getId());
                    response.setTitle(post.getTitle());
                    response.setContent(post.getContent());
                    response.setReadTime(post.getReadTime());
                    response.setCreatedAt(post.getCreatedAt());
                    response.setUpdatedAt(post.getUpdatedAt());
                    response.setCategoryId(post.getCategory().getId());
                    response.setAuthor(post.getUser().getFirstname() + " " + post.getUser().getLastname());
                    response.setAuthorProfileUrl(post.getUser().getProfileIconUrl());
                    response.setImgUrl(post.getImgUrl());
                    response.setSummary(post.getSummary());
                    response.setReferences(post.getReferences());
                    response.setComments(mapComments(post.getComments()));
                    response.setLikes(mapLikes(post.getLikes()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    public static PostResponse generateUserPostWithUserInfo(Post post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setReadTime(post.getReadTime());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        response.setCategoryId(post.getCategory().getId());
        response.setAuthor(post.getUser().getFirstname() + " " + post.getUser().getLastname());
        response.setAuthorProfileUrl(post.getUser().getProfileIconUrl());
        response.setImgUrl(post.getImgUrl());
        response.setSummary(post.getSummary());
        response.setReferences(post.getReferences());
        response.setComments(mapComments(post.getComments()));
        response.setLikes(mapLikes(post.getLikes()));
        return response;
    }

    public static List<CommentResponse> mapComments(List<Comment> comments){
            return comments.stream()
                    .map(c -> new CommentResponse(
                            c.getId(),
                            c.getUser().getId(),
                            c.getContent(),
                            c.getUser().getFirstname() + " " + c.getUser().getLastname(),
                            c.getCreatedAt()))
                    .toList();
        }

        public static List<LikeResponse> mapLikes(List<Like> likes) {
            return likes.stream()
                    .map(l -> new LikeResponse(
                            l.getId(),
                            l.getUser().getId()))
                    .toList();
        }
}
