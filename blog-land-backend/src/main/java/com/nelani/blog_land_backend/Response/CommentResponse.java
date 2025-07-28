package com.nelani.blog_land_backend.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Long id;
    private Long userId;
    private String content;
    private String author;
    private String profileImgUrl;
    private LocalDateTime createdAt;
}
