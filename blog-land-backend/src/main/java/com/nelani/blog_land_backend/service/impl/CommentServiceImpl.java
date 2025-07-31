package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.Validation.CommentValidation;
import com.nelani.blog_land_backend.Util.Validation.FormValidation;
import com.nelani.blog_land_backend.Util.Builders.PostBuilder;
import com.nelani.blog_land_backend.Util.Validation.PostValidation;
import com.nelani.blog_land_backend.Util.Validation.UserValidation;
import com.nelani.blog_land_backend.dto.CommentDto;
import com.nelani.blog_land_backend.model.Comment;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.CommentRepository;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.response.CommentResponse;
import com.nelani.blog_land_backend.service.CommentService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    @PersistenceContext
    private EntityManager entityManager;

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository,
            UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public  Page<CommentResponse> getByPostId(Long postId, int page, int size) {
        // Validate fields
        Long id = FormValidation.assertRequiredField(postId, "Post Id");

        // Validate post existence
        Optional<Post> optionalPost = postRepository.findById(id);
        PostValidation.assertPostExists(optionalPost);

        // Fetch paginated comment by post
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findByPostId(id, pageable);

        // Convert to CommentResponse while retaining pagination metadata
        return commentPage.map(PostBuilder::mapComment);
    }

    @Override
    @Transactional
    public  Page<CommentResponse> getByUserId( int page, int size) {
        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Fetch paginated comment by user
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findByUserId(user.getId(), pageable);

        // Checks if users have posts
        if (commentPage.isEmpty()) {
            return Page.empty(pageable);
        }

        // Convert to CommentResponse while retaining pagination metadata
        return commentPage.map(PostBuilder::mapComment);
    }

    @Override
    @Transactional
    public void addComment(CommentDto commentDto) {
        // Validate fields
        Long postId = FormValidation.assertRequiredField(commentDto.getPostId(), "Post Id");
        String content = FormValidation.assertRequiredField(commentDto.getContent(), "Content");

        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Checks if the post exists
        Optional<Post> post = postRepository.findById(postId);
        Post existingPost = PostValidation.assertPostExist(post);

        // Build new post
        Comment newComment = Comment.builder()
                .content(content)
                .user(user)
                .post(existingPost)
                .build();

        user.getComments().add(newComment);
        userRepository.save(user); // Save the new comment
    };

    @Override
    @Transactional
    public void updateComment(CommentDto commentDto) {
        // Validate fields
        Long commentId = FormValidation.assertRequiredField(commentDto.getId(), "Comment Id");
        String content = FormValidation.assertRequiredField(commentDto.getContent(), "Content");
        Long postId = FormValidation.assertRequiredField(commentDto.getPostId(), "Post Id");

        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Checks if the post exists
        Optional<Post> post = postRepository.findById(postId);
        PostValidation.assertPostExists(post);

        // Checks if the Comment exists
        Optional<Comment> comment = commentRepository.findById(commentId);
        Comment existingComment = CommentValidation.assertCommentExist(comment);

        // Checks if the comment belongs to the user
        CommentValidation.assertCommentBelongsToUser(existingComment, user);

        // Update existing comment
        existingComment.setContent(content);

        commentRepository.save(existingComment); // Save comment
    }

    @Override
    @Transactional
    public void deleteComment(CommentDto commentDto) {
        // Validate fields
        Long commentId = FormValidation.assertRequiredField(commentDto.getId(), "Comment Id");

        // Get current authenticated user
        User user = UserValidation.getOrThrowUnauthorized();

        // Checks if the comment exists
        Optional<Comment> comment = commentRepository.findById(commentId);
        Comment existingComment = CommentValidation.assertCommentExist(comment);

        // Checks if the comment belongs to the user
        CommentValidation.assertCommentBelongsToUser(existingComment, user);

        User commentOwner = existingComment.getUser();
        existingComment.setUser(null); // break the link from comment to user

        commentOwner.getComments().remove(existingComment); // triggers orphanRemoval
        commentRepository.delete(existingComment);
        entityManager.flush(); // should cascade delete comments and likes
        entityManager.clear(); // refresh context
    }
}
