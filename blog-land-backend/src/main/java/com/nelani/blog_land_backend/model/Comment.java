package com.nelani.blog_land_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments", schema = "blog")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @NotBlank(message = "Comment content cannot be empty")
    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String content;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "A comment must be associated with a user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    @NotNull(message = "A comment must belong to a post")
    private Post post;

    @Override
    public String toString() {
        return "Comment{id=" + id +
                ", content='" + content +
                "', postId=" + (post != null ? post.getId() : null) +
                ", userId=" + (user != null ? user.getId() : null) +
                "}";
    }
}
