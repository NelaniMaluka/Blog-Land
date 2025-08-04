package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.Util.JwtUtil;
import com.nelani.blog_land_backend.Util.Validation.ModerationValidator;
import com.nelani.blog_land_backend.dto.CommentDto;
import com.nelani.blog_land_backend.model.*;
import com.nelani.blog_land_backend.repository.CategoryRepository;
import com.nelani.blog_land_backend.repository.CommentRepository;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.service.impl.CommentServiceImpl;

import jakarta.persistence.EntityManager;
import jakarta.validation.ValidationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CommentServiceImplTest {

        @Mock
        private UserRepository userRepository;

        @Mock
        private PostRepository postRepository;

        @Mock
        private CategoryRepository categoryRepository;

        @Mock
        private CommentRepository commentRepository;

        @Mock
        private ModerationValidator moderationValidator;

        @Mock
        private JwtUtil jwtUtils;

        @Mock
        private EntityManager entityManager;

        @InjectMocks
        private CommentServiceImpl commentService;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);

                // Arrange
                User existingUser = new User();
                existingUser.setEmail("nelani@example.com");
                existingUser.setFirstname("Nelani");
                existingUser.setLastname("Maluka");
                existingUser.setProvider(Provider.LOCAL);
                existingUser.setId(100L);

                Post existingPost = new Post();
                existingPost.setId(1L);
                existingPost.setTitle("Spring Boot Guide");
                existingPost.setUser(existingUser);
                existingUser.setPosts(new ArrayList<>(List.of(existingPost)));

                Category category = new Category();
                category.setId(1L);
                category.setName("Tech");

                Comment existingComment = new Comment();
                existingComment.setId(2L);
                existingComment.setContent("Test content");
                existingComment.setUser(existingUser);
                existingComment.setPost(existingPost);

                // Set authenticated user in security context
                var auth = new UsernamePasswordAuthenticationToken(
                                existingUser, null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);

                // Mock repository to return this user
                when(commentRepository.findById(2L)).thenReturn(Optional.of(existingComment));
                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(existingUser));
                when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
                when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));
                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
        }

        @Test
        void addComment_ShouldSaveValidComment() {
                // Arrange
                CommentDto commentDto = new CommentDto();
                commentDto.setContent("I like your post");
                commentDto.setPostId(1L);

                doNothing().when(moderationValidator).userModeration(any(User.class));

                // Act
                commentService.addComment(commentDto);

                // Assert
                ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
                verify(userRepository, times(1)).save(userCaptor.capture());
                User savedUser = userCaptor.getValue();

                Comment savedComment = savedUser.getComments().get(0);
                assertEquals("I like your post", savedComment.getContent());
                assertEquals(1L, savedComment.getPost().getId());
        }

        @Test
        void addComment_ShouldThrowException_WhenFailModeration() {
                // Arrange
                CommentDto commentDto = new CommentDto();
                commentDto.setContent("fuck");
                commentDto.setPostId(1L);

                doThrow(new ValidationException("Comment did not pass moderation."))
                                .when(moderationValidator).commentModeration(any(Comment.class));

                // Act + Assert
                ValidationException exception = assertThrows(
                                ValidationException.class,
                                () -> commentService.addComment(commentDto));

                assertEquals("Comment did not pass moderation.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(commentRepository, never()).save(any());
        }

        @Test
        void addComment_ShouldThrowException_WhenUserContentEmpty() {
                // Arrange
                CommentDto commentDto = new CommentDto();
                commentDto.setContent("    ");
                commentDto.setPostId(1L);

                doNothing().when(moderationValidator).userModeration(any(User.class));

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> commentService.addComment(commentDto));

                assertEquals("Content is required.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(commentRepository, never()).save(any());
        }

        @Test
        void addComment_ShouldThrowException_WhenUserPostIdNull() {
                // Arrange
                CommentDto commentDto = new CommentDto();
                commentDto.setContent("I like your post");
                commentDto.setPostId(null);

                doNothing().when(moderationValidator).userModeration(any(User.class));

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> commentService.addComment(commentDto));

                assertEquals("Post Id is required.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(commentRepository, never()).save(any());
        }

        @Test
        void addComment_ShouldThrowException_WhenUserIsEmpty() {
                // Arrange
                CommentDto commentDto = new CommentDto();
                commentDto.setContent("I like your post");
                commentDto.setPostId(1L);

                doNothing().when(moderationValidator).userModeration(any(User.class));
                SecurityContextHolder.clearContext();

                // Act + Assert
                BadCredentialsException exception = assertThrows(
                                BadCredentialsException.class,
                                () -> commentService.addComment(commentDto));

                assertEquals("No authenticated user found.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(commentRepository, never()).save(any());
        }

        @Test
        void updateComment_ShouldUpdateValidComment() {
                // Arrange
                CommentDto commentDto = new CommentDto();
                commentDto.setId(1L);
                commentDto.setContent("I like your post");
                commentDto.setPostId(1L);

                // Get the user from SecurityContext (set up in @BeforeEach)
                User authenticatedUser = (User) SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getPrincipal();

                Comment existingComment = new Comment();
                existingComment.setId(1L);
                existingComment.setContent("Test content");
                existingComment.setUser(authenticatedUser);

                Post mockPost = new Post();
                mockPost.setId(1L);
                mockPost.setUser(authenticatedUser);
                existingComment.setPost(mockPost);

                when(commentRepository.findById(1L)).thenReturn(Optional.of(existingComment));
                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(authenticatedUser));
                doNothing().when(moderationValidator).userModeration(any(User.class));
                doNothing().when(moderationValidator).userModeration(any(User.class));

                // Act
                commentService.updateComment(commentDto);

                // Assert
                ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
                verify(commentRepository, times(1)).save(commentCaptor.capture());
                Comment savedComment = commentCaptor.getValue();

                assertEquals("I like your post", savedComment.getContent());
                assertEquals(1L, savedComment.getPost().getId());
        }

        @Test
        void updateComment_ShouldThrowException_WhenFailModeration() {
                // Arrange
                CommentDto commentDto = new CommentDto();
                commentDto.setId(null);
                commentDto.setContent("fuck");
                commentDto.setPostId(1L);

                doThrow(new ValidationException("Comment did not pass moderation."))
                                .when(moderationValidator).commentModeration(any(Comment.class));

                // Act + Assert
                ValidationException exception = assertThrows(
                                ValidationException.class,
                                () -> commentService.addComment(commentDto));

                assertEquals("Comment did not pass moderation.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(commentRepository, never()).save(any());
        }

        @Test
        void updateComment_ShouldThrowException_WhenIdIsNull() {
                // Arrange
                CommentDto commentDto = new CommentDto();
                commentDto.setId(null);
                commentDto.setContent("I like your post");
                commentDto.setPostId(1L);

                doNothing().when(moderationValidator).userModeration(any(User.class));

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> commentService.updateComment(commentDto));

                assertEquals("Comment Id is required.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(commentRepository, never()).save(any());
        }

        @Test
        void updateComment_ShouldThrowException_WhenContentIsEmpty() {
                // Arrange
                CommentDto commentDto = new CommentDto();
                commentDto.setId(1L);
                commentDto.setContent("    ");
                commentDto.setPostId(1L);

                doNothing().when(moderationValidator).userModeration(any(User.class));

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> commentService.updateComment(commentDto));

                assertEquals("Content is required.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(commentRepository, never()).save(any());
        }

        @Test
        void updateComment_ShouldThrowException_WhenPostIdIsNull() {
                // Arrange
                CommentDto commentDto = new CommentDto();
                commentDto.setId(1L);
                commentDto.setContent("I like your post");
                commentDto.setPostId(null);

                doNothing().when(moderationValidator).userModeration(any(User.class));

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> commentService.updateComment(commentDto));

                assertEquals("Post Id is required.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(commentRepository, never()).save(any());
        }

        @Test
        void deleteComment_ShouldDeleteComment() {
                // Arrange
                CommentDto commentDto = new CommentDto();
                commentDto.setId(1L);

                // Get the user from SecurityContext (set up in @BeforeEach)
                User authenticatedUser = (User) SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getPrincipal();

                Comment existingComment = new Comment();
                existingComment.setId(1L);
                existingComment.setContent("Test content");
                existingComment.setUser(authenticatedUser);

                Post mockPost = new Post();
                mockPost.setId(1L);
                mockPost.setUser(authenticatedUser);
                existingComment.setPost(mockPost);

                when(commentRepository.findById(1L)).thenReturn(Optional.of(existingComment));
                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(authenticatedUser));
                doNothing().when(moderationValidator).userModeration(any(User.class));
                doNothing().when(entityManager).flush();
                doNothing().when(entityManager).clear();

                // Act
                commentService.deleteComment(commentDto);

                // Assert
                verify(commentRepository).delete(existingComment);
                verify(entityManager).flush();
                verify(entityManager).clear();
        }

        @Test
        void deleteComment_ShouldThrowException_WhenNoCommentExists() {
                // Arrange
                CommentDto commentDto = new CommentDto();
                commentDto.setId(1L);

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> commentService.deleteComment(commentDto));

                assertEquals("Comment does not exist.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(commentRepository, never()).delete(any());
        }

}
