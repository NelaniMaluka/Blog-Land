package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.response.LikeResponse;

import java.util.List;
import java.util.UUID;

public interface LikeService {
    long getPostLikesCount(UUID postId);

    List<LikeResponse> getUserLikes();

    void addLike(UUID postId);

    void removeLike(UUID likeId);

}
