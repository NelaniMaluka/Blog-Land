package com.nelani.blog_land_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime updatedAt;
    private Long categoryId;
    private String summary;
    private String imgUrl;
    private String references;
}
