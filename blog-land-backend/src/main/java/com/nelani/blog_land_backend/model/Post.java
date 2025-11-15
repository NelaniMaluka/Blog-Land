package com.nelani.blog_land_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "blog_posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Content is required")
    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Min(value = 0, message = "Word count cannot be negative")
    @Column(nullable = false)
    private int wordCount;

    @Min(value = 1, message = "Read time must be at least 1 minute")
    @Column(nullable = false)
    private int readTime;

    @NotBlank(message = "Summary is required")
    @Size(max = 500, message = "Summary must not exceed 500 characters")
    @Column(nullable = false, length = 500)
    private String summary;

    @NotBlank(message = "Image URL is required")
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    @Column(nullable = false, length = 500)
    private String imgUrl;

    @Builder.Default
    @Min(value = 0, message = "View count cannot be negative")
    @Column(nullable = false)
    private Long viewCount = 0L;

    @Lob
    @Column(name = "post_references")
    private String references;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;
    private LocalDateTime scheduledAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean isDraft = false;

    @NotNull(message = "User is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Category is required")
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public void setContent(String content) {
        this.content = content;
        this.wordCount = calculateWordCount(content);
        this.readTime = calculateReadTime(this.wordCount);
    }

    private int calculateWordCount(String content) {
        return content == null ? 0 : content.trim().split("\\s+").length;
    }

    private int calculateReadTime(int words) {
        int averageWordsPerMinute = 200; // configurable reading speed
        return Math.max(1, words / averageWordsPerMinute);
    }

    @Override
    public String toString() {
        return "Post{id=" + id + ", title='" + title + "', userId=" + (user != null ? user.getId() : null) + "}";
    }
}
