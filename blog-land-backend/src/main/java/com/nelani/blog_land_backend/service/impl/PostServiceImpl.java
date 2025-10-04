package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.Caches.PostCacheHelper;
import com.nelani.blog_land_backend.sockets.PostSocket;
import com.nelani.blog_land_backend.Util.Validation.*;
import com.nelani.blog_land_backend.Util.Builders.PostBuilder;
import com.nelani.blog_land_backend.dto.PostDto;
import com.nelani.blog_land_backend.dto.TechCrunchPostDto;
import com.nelani.blog_land_backend.model.*;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.response.PostResponse;
import com.nelani.blog_land_backend.service.PostService;

import jakarta.persistence.EntityManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostCacheHelper postCacheHelper;
    private final EntityManager entityManager;
    private final PostRepository postRepository;
    private final ModerationValidator moderationValidator;
    private final PostSocket postSocket;
    private final CategoryValidation categoryValidation;
    private final PostValidation postValidation;

    public PostServiceImpl(PostCacheHelper postCacheHelper, EntityManager entityManager, PostRepository postRepository,
            ModerationValidator moderationValidator, PostSocket postSocket, CategoryValidation categoryValidation,
            PostValidation postValidation) {
        this.postCacheHelper = postCacheHelper;
        this.entityManager = entityManager;
        this.postRepository = postRepository;
        this.moderationValidator = moderationValidator;
        this.postSocket = postSocket;
        this.categoryValidation = categoryValidation;
        this.postValidation = postValidation;
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
                .sorted(Comparator.comparingInt(PostResponse::getScore).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void incrementViews(Long postId) {
        // Checks if the post exists
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        // Adds a view to the post
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post); // Save the post with the updated views

        postCacheHelper.evictAllUserPosts(post.getUser().getId(), postId, post.getCategory().getId()); // Evict all data

        // Update the socket
        postSocket.updatePost(post);
    }

    @Override
    @Transactional
    @Cacheable(value = "categoryPosts", key = "#categoryId + '_' + #page + '_' + #size + '_' + #order")
    public Page<PostResponse> getByCategoryId(Long categoryId, int page, int size, String order) {
        String setOrder = (order == null || !order.equals("oldest")) ? "latest" : "oldest";

        // Checks if the category exists
        categoryValidation.assertCategoryExists(categoryId);

        // Determine sort direction
        Sort.Direction direction = setOrder.equals("latest") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "createdAt"));

        // Fetch only posts in the given category
        Page<Post> postPage = postRepository.findByCategoryId(categoryId, pageable);

        // Convert to PostResponse while retaining pagination metadata
        return postPage.map(PostBuilder::generatePost);
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

    @Override
    @Transactional
    @Cacheable(value = "userPosts", key = "T(com.nelani.blog_land_backend.Util.Validation.UserValidation).getCurrentUserId() + '_' + #page + '_' + #size")
    public Page<PostResponse> getByUserId(int page, int size) {
        // Get current authenticated user
        User user = UserValidation.getAuthenticatedUser();

        // Fetch paginated posts by category
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);

        // Checks if the user has posts
        if (postPage.isEmpty()) {
            return Page.empty(pageable);
        }

        // Convert to PostResponse while retaining pagination metadata
        return postPage.map(PostBuilder::generatePost);
    }

    @Override
    @Transactional
    public void addPost(PostDto postDto) {
        // Get current authenticated user
        User user = UserValidation.getAuthenticatedUser();

        // Checks if the category exists
        Category category = categoryValidation.assertCategoryExists(postDto.getCategoryId());

        // Checks if the user has a post with the same title
        List<Post> userPosts = postRepository.findAllByUserId(user.getId());
        postValidation.assertUserHasPostWithSameTitle(userPosts, postDto.getTitle());

        // Build new post
        Post newPost = Post.builder()
                .title(postDto.getTitle())
                .user(user)
                .category(category)
                .imgUrl(postDto.getImgUrl())
                .references(postDto.getReferences())
                .summary(postDto.getSummary())
                .isDraft(postDto.isDraft())
                .scheduledAt(postDto.getScheduledAt())
                .viewCount(0L)
                .build();
        newPost.setContent(postDto.getContent());

        // Moderate content
        moderationValidator.postModeration(newPost);

        postRepository.save(newPost); // Save the new post

        postCacheHelper.evictAllUserPosts(user.getId(), newPost.getId(), postDto.getCategoryId()); // Evict cache data

        // Update the socket
        postSocket.addNewPost(newPost);
    };

    @Override
    @Transactional
    public void updatePost(PostDto postDto) {
        // Get current authenticated user
        User user = UserValidation.getAuthenticatedUser();

        // Checks if the category exists
        Category category = categoryValidation.assertCategoryExists(postDto.getCategoryId());

        // Checks if the post exists
        Post post = postValidation.assertPostExist(postDto.getId());

        // Checks if the post belongs to the user
        postValidation.assertPostBelongsToUser(post, user);

        // Update existing post
        Post updatedPost = post;
        updatedPost.setCategory(category);
        updatedPost.setTitle(postDto.getTitle());
        updatedPost.setContent(postDto.getContent());
        updatedPost.setImgUrl(postDto.getImgUrl());
        updatedPost.setReferences(postDto.getReferences());
        updatedPost.setSummary(postDto.getSummary());
        updatedPost.setUpdatedAt(postDto.getUpdatedAt());
        updatedPost.setDraft(postDto.isDraft());
        updatedPost.setScheduledAt(postDto.getScheduledAt());

        // Moderate content
        moderationValidator.postModeration(updatedPost);

        postRepository.save(updatedPost); // Save updated post

        postCacheHelper.evictAllUserPosts(user.getId(), updatedPost.getId(), postDto.getCategoryId()); // Evict cache
                                                                                                       // data

        // Update the socket
        postSocket.updatePost(updatedPost);
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
        // Get current authenticated user
        User user = UserValidation.getAuthenticatedUser();

        // Checks if the post exists
        Post existingPost = postValidation.assertPostExist(postId);

        // Checks if the post belongs to the user
        postValidation.assertPostBelongsToUser(existingPost, user);

        User postOwner = existingPost.getUser();

        postOwner.getPosts().remove(existingPost); // triggers orphanRemoval
        entityManager.flush(); // should cascade delete comments and likes
        entityManager.clear(); // refresh context

        postCacheHelper.evictAllUserPosts(user.getId(), existingPost.getId(), existingPost.getCategory().getId()); // Evict
                                                                                                                   // cache
                                                                                                                   // data

        // Update the socket
        postSocket.deletePost(postId);
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
