package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.cache.PostCacheHelper;
import com.nelani.blog_land_backend.sockets.PostSocket;
import com.nelani.blog_land_backend.mapper.PostBuilder;
import com.nelani.blog_land_backend.dto.TechCrunchPostDto;
import com.nelani.blog_land_backend.model.*;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.response.PostResponse;
import com.nelani.blog_land_backend.service.PostService;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostCacheHelper postCacheHelper;
    private final PostRepository postRepository;
    private final PostSocket postSocket;

    public PostServiceImpl(PostCacheHelper postCacheHelper, PostRepository postRepository, PostSocket postSocket) {
        this.postCacheHelper = postCacheHelper;
        this.postRepository = postRepository;
        this.postSocket = postSocket;
    }

    @Override
    @Transactional
    public List<PostResponse> searchByKeyword(String keyword) {
        // Get the first 5 posts from the keyword
        Pageable pageable = PageRequest.of(0, 5);
        Page<Post> posts = postRepository.searchByKeyword(keyword, pageable);

        // Rank results using custom scoring
        return posts.stream()
                .map(post -> {
                    int score = calculateRelevanceScore(post, keyword);
                    return PostResponse.builder()
                            .id(post.getId())
                            .title(post.getTitle())
                            .readTime(post.getReadTime())
                            .score(score)
                            .build();
                })
                .sorted(Comparator.comparingInt(PostResponse::score).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void incrementViews(UUID postId) {
        // Checks if the post exists
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        // Adds a view to the post
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post); // Save the post with the updated views

        postCacheHelper.evictAllUserPosts(post.getUser().getId(), postId, post.getCategory().getId()); // Evict all data

        // Update the socket
        postSocket.updatePost(post);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "latestPosts", key = "#page + '-' + #size")
    public List<PostResponse> getLatestPost(int page, int size) {
        // Techcrunch api
        String apiUrl = "https://techcrunch.com/wp-json/wp/v2/posts?per_page=" + size + "&page=" + page + "&_embed";
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<TechCrunchPostDto[]> response = restTemplate.getForEntity(apiUrl, TechCrunchPostDto[].class);
        TechCrunchPostDto[] externalPosts = response.getBody();

        if (externalPosts == null) {
            throw new AssertionError();
        }

        return Arrays.stream(externalPosts).map(dto -> {
            // Author comes from embedded.author
            String author = dto.getEmbedded().getAuthor()[0].getName();

            String title = dto.getTitle().getRendered();
            String content = dto.getContent().getRendered();
            String summary = dto.getExcerpt().getRendered();
            LocalDateTime createdAt = LocalDateTime.parse(dto.getDate());

            // Image comes from embedded.featuredmedia
            String image = null;
            if (dto.getEmbedded().getFeaturedmedia() != null
                    && dto.getEmbedded().getFeaturedmedia().length > 0) {
                image = dto.getEmbedded().getFeaturedmedia()[0].getSourceUrl();
            }

            return PostResponse.builder()
                    .title(title)
                    .content(content)
                    .summary(summary)
                    .source("TechCrunch")
                    .createdAt(createdAt)
                    .readTime(PostBuilder.calculateReadTime(content))
                    .postImgUrl(image)
                    .build();
        }).toList();
    }

    private int calculateRelevanceScore(Post post, String keyword) {
        int score = 0;
        String lowerKeyword = keyword.toLowerCase();

        if (post.getTitle().toLowerCase().contains(lowerKeyword))
            score += 3;
        if (post.getSummary().toLowerCase().contains(lowerKeyword))
            score += 2;
        if (post.getContent().toLowerCase().contains(lowerKeyword))
            score += 1;
        if (post.getCategory() != null && post.getCategory().getName().toLowerCase().contains(lowerKeyword))
            score += 2;

        return score;
    }
}
