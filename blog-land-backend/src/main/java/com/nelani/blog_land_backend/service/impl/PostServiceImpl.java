package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.Validation.CategoryValidation;
import com.nelani.blog_land_backend.Util.Validation.FormValidation;
import com.nelani.blog_land_backend.Util.Builders.PostBuilder;
import com.nelani.blog_land_backend.Util.Validation.PostValidation;
import com.nelani.blog_land_backend.Util.Validation.UserValidation;
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
    public  List<PostResponse> searchByKeyword(String keyword) {
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
        // Checks if the post  exists
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        // Adds a view to the post
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post); // Saves the post with the updated views
    }

    @Override
    @Transactional
    public Page<PostResponse>  getByCategoryId(Long categoryId, int page, int size) {
        // Validate Fields
        Long category_id = FormValidation.assertRequiredField(categoryId, "Category Id");

        // Checks if the category exists
        Optional<Category> optionalCategory = categoryRepository.findById(category_id);
        CategoryValidation.assertCategoryExists(optionalCategory);

        // Fetch paginated posts by category
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findByCategoryIdOrderByCreatedAtDesc(category_id, pageable);

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
        if (externalPosts == null) throw new AssertionError();
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
                .viewCount(0L)
                .build();
        newPost.setContent(content);

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
        updatedPost.setTitle(title);
        updatedPost.setContent(content);
        updatedPost.setImgUrl(imgUrl);
        updatedPost.setReferences(references);
        updatedPost.setSummary(summary);
        updatedPost.setUpdatedAt(updatedAt);

        postRepository.save(updatedPost); // Save updated post
    }

    @Override
    @Transactional
    public void deletePost(PostDto postDto) {
        // Validate fields
        Long postId = FormValidation.assertRequiredField(postDto.getId(), "Post Id");

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
