package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.response.LikeResponse;

import java.util.List;

public interface LikeService {
    long getPostLikesCount(long postId);

    List<LikeResponse> getUserLikes();

    String addLike(long postId);

    String removeLike(long likeId);

}
