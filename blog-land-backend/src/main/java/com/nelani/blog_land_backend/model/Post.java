package com.nelani.blog_land_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int wordCount;

    @Column(nullable = false)
    private int readTime;

    @Column(nullable = false)
    private String summary;

    @Column(nullable = false)
    private String imgUrl;

    @Lob
    private String references;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Like> likes = new ArrayList<>();

    public void setContent(String content) {
        this.content = content;
        this.wordCount = calculateWordCount(content);
        this.readTime = calculateReadTime(this.wordCount);
    }

    private int calculateWordCount(String content) {
        return content == null ? 0 : content.trim().split("\\s+").length;
    }

    private int calculateReadTime(int words) {
        int averageWordsPerMinute = 200; // You can adjust this
        return Math.max(1, words / averageWordsPerMinute);
    }

}

