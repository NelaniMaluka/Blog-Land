package com.nelani.blog_land_backend.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponse implements Serializable {
    private Long id;
    private String title;
    private String content;
    private int readTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long categoryId;
    private String summary;
    private String postImgUrl;
    private Long views;
    private Long likes;
    private String references;
    private String source;
    private int score;
    private String userId;
    private boolean isDraft;
}
