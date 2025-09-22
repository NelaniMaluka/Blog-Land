package com.nelani.blog_land_backend.response;

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
public class LikeResponse implements Serializable {
    private long likeId;
    private LocalDateTime likedAt;
    private long postId;

}
