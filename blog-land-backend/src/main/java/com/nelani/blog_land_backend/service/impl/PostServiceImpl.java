package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.FormValidation;
import com.nelani.blog_land_backend.Util.PostBuilder;
import com.nelani.blog_land_backend.Util.ResponseBuilder;
import com.nelani.blog_land_backend.Util.UserValidation;
import com.nelani.blog_land_backend.dto.PostDto;
import com.nelani.blog_land_backend.dto.TechCrunchPostDto;
import com.nelani.blog_land_backend.model.*;
import com.nelani.blog_land_backend.repository.CategoryRepository;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.response.PostResponse;
import com.nelani.blog_land_backend.service.PostService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    @PersistenceContext
    private EntityManager entityManager;

    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostServiceImpl(CategoryRepository categoryRepository, PostRepository postRepository,
            UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<?> searchByKeyword(String keyword) {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Post> posts = postRepository.searchByKeyword(keyword, pageable);

        // Rank results using custom scoring
        List<PostResponse> rankedResults = posts.stream()
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

        return ResponseEntity.ok(rankedResults);
    }

    @Override
    @Transactional
    public void incrementViews(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
    }

    @Override
    @Transactional
    public ResponseEntity<?> getByCategoryId(Long categoryId, int page, int size) {
        // Trim and validate
        Long id = FormValidation.trimAndValidate(categoryId, "Category Id");

        // Validate category existence
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            return ResponseBuilder.invalid("Validation Error",
                    "Category with ID " + id + " does not exist.");
        }

        Pageable pageable = PageRequest.of(page, size);

        // Fetch paginated posts by category
        Page<Post> postPage = postRepository.findByCategoryIdOrderByCreatedAtDesc(id, pageable);

        // Convert to PostResponse while retaining pagination metadata
        Page<PostResponse> responsePage = postPage.map(PostBuilder::generatePost);

        return ResponseEntity.ok(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<?> getLatestPost(int page, int size) {
        try {
            String apiUrl = "https://techcrunch.com/wp-json/wp/v2/posts?per_page=" + size + "&page=" + page + "&_embed";
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<TechCrunchPostDto[]> response = restTemplate.getForEntity(apiUrl, TechCrunchPostDto[].class);
            TechCrunchPostDto[] externalPosts = response.getBody();

            List<PostResponse> postResponses = Arrays.stream(externalPosts).map(dto -> {
                String author = dto.get_embedded().getAuthor()[0].getName();
                String title = dto.getTitle().getRendered();
                String content = dto.getContent().getRendered();
                String summary = dto.getExcerpt().getRendered();
                LocalDateTime createdAt = LocalDateTime.parse(dto.getDate());

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

            return ResponseEntity.ok(postResponses);

        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> getByUserId(int page, int size) {
        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Checks if users have posts
        if (user.getPosts().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        Pageable pageable = PageRequest.of(page, size);

        // Fetch paginated posts by category
        Page<Post> postPage = postRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);

        // Convert to PostResponse while retaining pagination metadata
        Page<PostResponse> responsePage = postPage.map(PostBuilder::generatePost);

        return ResponseEntity.ok(responsePage);
    }

    @Override
    @Transactional
    public ResponseEntity<?> addPost(PostDto postDto) {
        // Trim and validate
        String title = FormValidation.trimAndValidate(postDto.getTitle(), "Title");
        String content = FormValidation.trimAndValidate(postDto.getContent(), "Content");
        Long userId = FormValidation.trimAndValidate(postDto.getUserId(), "User Id");
        Long categoryId = FormValidation.trimAndValidate(postDto.getCategoryId(), "Category Id");
        String summary = FormValidation.trimAndValidate(postDto.getSummary(), "Summary");
        String imgUrl = FormValidation.trimAndValidate(postDto.getImgUrl(), "Img Url");
        String references = postDto.getReferences();

        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Checks if the token and user id match
        if (!user.getId().equals(userId)) {
            return ResponseBuilder.invalid("Authorization Error",
                    "The user ID provided does not match the authenticated account. Please verify your credentials.");
        }

        // Checks if the category exists
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            return ResponseBuilder.invalid("validation.error", "category.notFound");
        }

        // Checks if the user has a post with the same title
        for (Post post : user.getPosts()) {
            if (post.getTitle().equals(title)) {
                return ResponseBuilder.invalid("Validation error",
                        "You cannot use the same title twice");
            }
        }

        // Build new post
        Post newPost = Post.builder()
                .title(title)
                .user(user)
                .category(category.get())
                .imgUrl(imgUrl)
                .references(references)
                .summary(summary)
                .viewCount(0L)
                .build();
        newPost.setContent(content);

        user.getPosts().add(newPost);
        userRepository.save(user);

        return ResponseEntity.ok("Success, Your post was successfully added");
    };

    @Override
    @Transactional
    public ResponseEntity<?> updatePost(PostDto postDto) {
        // Trim and validate
        Long id = FormValidation.trimAndValidate(postDto.getId(), "Post Id");
        String title = FormValidation.trimAndValidate(postDto.getTitle(), "Title");
        String content = FormValidation.trimAndValidate(postDto.getContent(), "Content");
        Long userId = FormValidation.trimAndValidate(postDto.getUserId(), "User ID");
        Long categoryId = FormValidation.trimAndValidate(postDto.getCategoryId(), "Category Id");
        String summary = FormValidation.trimAndValidate(postDto.getSummary(), "Summary");
        String imgUrl = FormValidation.trimAndValidate(postDto.getImgUrl(), "Img Url");
        String references = postDto.getReferences();
        LocalDateTime updatedAt = LocalDateTime.now();

        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Checks if the token and user id match
        if (!user.getId().equals(userId)) {
            return ResponseBuilder.invalid("Authorization Error",
                    "The user ID provided does not match the authenticated account. Please verify your credentials.");
        }

        // Checks if the category exists
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            return ResponseBuilder.invalid("validation.error", "category.notFound");
        }

        // Checks if the post exists
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            return ResponseBuilder.invalid("Post not found",
                    "No post with ID " + id + " exists.");
        }

        // Checks if the post belongs to the user
        if (!post.get().getUser().getId().equals(userId)) {
            return ResponseBuilder.unauthorized("Authorization Error",
                    "The user ID provided does not match the author's id . Please verify your credentials.");
        }

        // Build new post
        Post updatedPost = post.get();
        updatedPost.setTitle(title);
        updatedPost.setContent(content);
        updatedPost.setImgUrl(imgUrl);
        updatedPost.setReferences(references);
        updatedPost.setSummary(summary);
        updatedPost.setUpdatedAt(updatedAt);

        postRepository.save(updatedPost);

        return ResponseEntity.ok("Success, Your post was successfully updated");
    }

    @Override
    @Transactional
    public ResponseEntity<?> deletePost(PostDto postDto) {
        // Trim and validate
        Long id = FormValidation.trimAndValidate(postDto.getId(), "Post Id");
        Long userId = FormValidation.trimAndValidate(postDto.getUserId(), "User Id");

        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Checks if the token and user id match
        if (!user.getId().equals(userId)) {
            return ResponseBuilder.invalid("Authorization Error",
                    "The user ID provided does not match the authenticated account. Please verify your credentials.");
        }

        // Checks if the post exists
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            return ResponseBuilder.invalid("Post not found",
                    "No post with ID " + id + " exists.");
        }

        // Checks if the post belongs to the user
        if (!post.get().getUser().getId().equals(userId)) {
            return ResponseBuilder.unauthorized("Authorization Error",
                    "The user ID provided does not match the author's id . Please verify your credentials.");
        }

        Post existingPost = post.get();
        User postOwner = existingPost.getUser();

        try {
            postOwner.getPosts().remove(existingPost); // triggers orphanRemoval
            entityManager.flush(); // should cascade delete comments and likes
            entityManager.clear(); // refresh context
        } catch (Exception e) {
            throw new RuntimeException("Post deletion failed.");
        }

        return ResponseEntity.ok("Success, Your post was successfully deleted");
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
