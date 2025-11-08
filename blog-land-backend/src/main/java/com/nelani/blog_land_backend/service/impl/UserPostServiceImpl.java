package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.dto.PostDto;
import com.nelani.blog_land_backend.mapper.PostBuilder;
import com.nelani.blog_land_backend.model.Category;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.CategoryRepository;
import com.nelani.blog_land_backend.repository.CommentRepository;
import com.nelani.blog_land_backend.repository.LikeRepository;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.response.PostResponse;
import com.nelani.blog_land_backend.service.UserPostService;
import com.nelani.blog_land_backend.sockets.PostSocket;
import com.nelani.blog_land_backend.cache.PostCacheHelper;
import com.nelani.blog_land_backend.util.validation.ModerationValidator;
import com.nelani.blog_land_backend.util.validation.UserValidation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class UserPostServiceImpl implements UserPostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final ModerationValidator moderationValidator;
    private final PostCacheHelper postCacheHelper;
    private final PostSocket postSocket;
    private final UserValidation userValidation;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    public UserPostServiceImpl(PostRepository postRepository, CategoryRepository categoryRepository,
            ModerationValidator moderationValidator, PostCacheHelper postCacheHelper, PostSocket postSocket,
            UserValidation userValidation, CommentRepository commentRepository, LikeRepository likeRepository) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.moderationValidator = moderationValidator;
        this.postCacheHelper = postCacheHelper;
        this.postSocket = postSocket;
        this.userValidation = userValidation;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
    }

    @Override
    @Transactional
    @Cacheable(value = "userPosts", key = "T(org.springframework.security.core.context.SecurityContextHolder).context.authentication.name + '_' + #page + '_' + #size")
    public Page<PostResponse> getByUserId(int page, int size) {
        // Get current authenticated user
        User user = userValidation.getAuthenticatedUser();

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
        User user = userValidation.getAuthenticatedUser();

        // Checks if the category exists
        Category category = categoryRepository.findById(postDto.categoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found."));

        // Checks if the user has a post with the same title
        List<Post> userPosts = postRepository.findAllByUserId(user.getId());
        for (Post post : userPosts) {
            if (post.getTitle().equals(postDto.title())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "You cannot use the same title twice.");
            }
        }

        // Build new post
        Post newPost = Post.builder()
                .title(postDto.title())
                .user(user)
                .category(category)
                .imgUrl(postDto.imgUrl())
                .references(postDto.references())
                .summary(postDto.summary())
                .isDraft(postDto.draft())
                .scheduledAt(postDto.scheduledAt())
                .viewCount(0L)
                .build();
        newPost.setContent(postDto.content());

        // Moderate content
        moderationValidator.postModeration(newPost);

        postRepository.save(newPost); // Save the new post

        postCacheHelper.evictAllUserPosts(user.getId(), newPost.getId(), postDto.categoryId()); // Evict cache data

        // Update the socket
        postSocket.addNewPost(newPost);
    }

    @Override
    @Transactional
    public void updatePost(PostDto postDto) {
        // Get current authenticated user
        User user = userValidation.getAuthenticatedUser();

        // Checks if the category exists
        Category category = categoryRepository.findById(postDto.categoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found."));

        // Checks if the post exists
        Post post = postRepository.findById(postDto.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found."));

        // Check ownership
        if (!post.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You are not authorized to perform this action on this post.");
        }

        // Update existing post
        post.setCategory(category);
        post.setTitle(postDto.title());
        post.setContent(postDto.content());
        post.setImgUrl(postDto.imgUrl());
        post.setReferences(postDto.references());
        post.setSummary(postDto.summary());
        post.setUpdatedAt(postDto.updatedAt());
        post.setDraft(postDto.draft());
        post.setScheduledAt(postDto.scheduledAt());

        // Moderate content
        moderationValidator.postModeration(post);

        postRepository.save(post); // Save updated post

        postCacheHelper.evictAllUserPosts(user.getId(), post.getId(), postDto.categoryId());

        // Update the socket
        postSocket.updatePost(post);
    }

    @Override
    @Transactional
    public void deletePost(UUID postId) {
        // Get current authenticated user
        User user = userValidation.getAuthenticatedUser();

        // Checks if the post exists
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found."));

        // Check ownership
        if (!existingPost.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You are not authorized to perform this action on this post.");
        }

        deletePostAndRelations(existingPost);

        postCacheHelper.evictAllUserPosts(user.getId(), existingPost.getId(), existingPost.getCategory().getId());

        // Update the socket
        postSocket.deletePost(postId);
    }

    @Transactional
    public void deletePostAndRelations(Post existingPost) {
        likeRepository.deleteByPost(existingPost);
        commentRepository.deleteByPost(existingPost);
        postRepository.delete(existingPost);
        postRepository.delete(existingPost);
    }

}
