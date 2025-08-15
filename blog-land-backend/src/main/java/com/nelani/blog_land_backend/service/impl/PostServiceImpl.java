package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.Validation.*;
import com.nelani.blog_land_backend.Util.Builders.PostBuilder;
import com.nelani.blog_land_backend.dto.CategoryWithPostsDTO;
import com.nelani.blog_land_backend.dto.PostDto;
import com.nelani.blog_land_backend.dto.TechCrunchPostDto;
import com.nelani.blog_land_backend.model.*;
import com.nelani.blog_land_backend.repository.CategoryRepository;
import com.nelani.blog_land_backend.repository.CustomPostRepository;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.response.PostResponse;
import com.nelani.blog_land_backend.service.PostService;

import jakarta.persistence.EntityManager;
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

    private final EntityManager entityManager;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CustomPostRepository customPostRepository;
    private final ModerationValidator moderationValidator;

    public PostServiceImpl(EntityManager entityManager, CategoryRepository categoryRepository,
            PostRepository postRepository, UserRepository userRepository, CustomPostRepository customPostRepository,
            ModerationValidator moderationValidator) {
        this.entityManager = entityManager;
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.customPostRepository = customPostRepository;
        this.moderationValidator = moderationValidator;
    }

    @Override
    @Transactional
    public List<CategoryWithPostsDTO> getTopCategoriesWithPosts() {
        // Gets the top 6 categories with minimum 5 posts
        return customPostRepository.findTopCategoriesWithPosts();
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
        postRepository.save(post); // Saves the post with the updated views
    }

    @Override
    @Transactional
    public Page<PostResponse> getByCategoryId(Long categoryId, int page, int size, String order) {
        // Validate Fields
        Long category_id = FormValidation.assertRequiredField(categoryId, "Category Id");
        String setOrder = (order == null || !order.equals("oldest")) ? "latest" : "oldest";

        // Checks if the category exists
        Optional<Category> optionalCategory = categoryRepository.findById(category_id);
        CategoryValidation.assertCategoryExists(optionalCategory);

        // Determine sort direction
        Sort.Direction direction = setOrder.equals("latest") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "createdAt"));

        // Fetch only posts in the given category
        Page<Post> postPage = postRepository.findByCategoryId(category_id, pageable);

        // Convert to PostResponse while retaining pagination metadata
        return postPage.map(PostBuilder::generatePost);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getLatestPost(int page, int size) {
        // Techcrunch api
        String apiUrl = "https://techcrunch.com/wp-json/wp/v2/posts?per_page=" + size + "&page=" + page + "&_embed";
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<TechCrunchPostDto[]> response = restTemplate.getForEntity(apiUrl, TechCrunchPostDto[].class);
        TechCrunchPostDto[] externalPosts = response.getBody();

        // Generates a list of the latest blog posts
        if (externalPosts == null)
            throw new AssertionError();
        return Arrays.stream(externalPosts).map(dto -> {
            String author = dto.get_embedded().getAuthor()[0].getName();
            String title = dto.getTitle().getRendered();
            String content = dto.getContent().getRendered();
            String summary = dto.getExcerpt().getRendered();
            LocalDateTime createdAt = LocalDateTime.parse(dto.getDate());

            // Builds response data
            return PostResponse.builder()
                    .title(title)
                    .content(content)
                    .summary(summary)
                    .author(author)
                    .source("TechCrunch")
                    .createdAt(createdAt)
                    .readTime(PostBuilder.calculateReadTime(content))
                    .build();
        }).toList();
    }

    @Override
    @Transactional
    public Page<PostResponse> getByUserId(int page, int size) {
        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

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
        // Validate Fields
        String title = FormValidation.assertRequiredField(postDto.getTitle(), "Title");
        String content = FormValidation.assertRequiredField(postDto.getContent(), "Content");
        Long categoryId = FormValidation.assertRequiredField(postDto.getCategoryId(), "Category Id");
        String summary = FormValidation.assertRequiredField(postDto.getSummary(), "Summary");
        String imgUrl = FormValidation.assertRequiredField(postDto.getImgUrl(), "Img Url");
        boolean isDraft = postDto.isDraft();
        LocalDateTime scheduledAt = postDto.getScheduledAt();
        String references = postDto.getReferences();

        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Checks if the category exists
        Optional<Category> category = categoryRepository.findById(categoryId);
        CategoryValidation.assertCategoryExists(category);

        // Checks if the user has a post with the same title
        PostValidation.assertUserHasPostWithSameTitle(user.getPosts(), title);

        // Build new post
        Post newPost = Post.builder()
                .title(title)
                .user(user)
                .category(category.get())
                .imgUrl(imgUrl)
                .references(references)
                .summary(summary)
                .isDraft(isDraft)
                .scheduledAt(scheduledAt)
                .viewCount(0L)
                .build();
        newPost.setContent(content);

        // Moderate content
        moderationValidator.postModeration(newPost);

        user.getPosts().add(newPost);
        userRepository.save(user); // Save the new post
    };

    @Override
    @Transactional
    public void updatePost(PostDto postDto) {
        // Validate fields
        Long postId = FormValidation.assertRequiredField(postDto.getId(), "Post Id");
        String title = FormValidation.assertRequiredField(postDto.getTitle(), "Title");
        String content = FormValidation.assertRequiredField(postDto.getContent(), "Content");
        Long categoryId = FormValidation.assertRequiredField(postDto.getCategoryId(), "Category Id");
        String summary = FormValidation.assertRequiredField(postDto.getSummary(), "Summary");
        String imgUrl = FormValidation.assertRequiredField(postDto.getImgUrl(), "Img Url");
        boolean isDraft = postDto.isDraft();
        LocalDateTime scheduledAt = postDto.getScheduledAt();
        String references = postDto.getReferences();
        LocalDateTime updatedAt = LocalDateTime.now();

        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Checks if the category exists
        Optional<Category> category = categoryRepository.findById(categoryId);
        CategoryValidation.assertCategoryExists(category);

        // Checks if the post exists
        Optional<Post> post = postRepository.findById(postId);
        PostValidation.assertPostExists(post);

        // Checks if the post belongs to the user
        PostValidation.assertPostBelongsToUser(post.get(), user);

        // Update existing post
        Post updatedPost = post.get();
        updatedPost.setCategory(category.get());
        updatedPost.setTitle(title);
        updatedPost.setContent(content);
        updatedPost.setImgUrl(imgUrl);
        updatedPost.setReferences(references);
        updatedPost.setSummary(summary);
        updatedPost.setUpdatedAt(updatedAt);
        updatedPost.setDraft(isDraft);
        updatedPost.setScheduledAt(scheduledAt);

        // Moderate content
        moderationValidator.postModeration(updatedPost);

        postRepository.save(updatedPost); // Save updated post
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        // Validate fields
        Long postId = FormValidation.assertRequiredField(id, "Post Id");

        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Checks if the post exists
        Optional<Post> post = postRepository.findById(postId);
        PostValidation.assertPostExists(post);

        // Checks if the post belongs to the user
        PostValidation.assertPostBelongsToUser(post.get(), user);

        Post existingPost = post.get();
        User postOwner = existingPost.getUser();

        postOwner.getPosts().remove(existingPost); // triggers orphanRemoval
        entityManager.flush(); // should cascade delete comments and likes
        entityManager.clear(); // refresh context
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
