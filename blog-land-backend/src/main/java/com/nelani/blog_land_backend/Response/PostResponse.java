package com.nelani.blog_land_backend.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private int readTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long categoryId;
    private String author;
    private String authorProfileUrl;
    private String summary;
    private String imgUrl;
    private String references;
    private List<CommentResponse> comments;
    private List<LikeResponse> likes;
}
