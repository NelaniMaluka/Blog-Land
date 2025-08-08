package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.Util.JwtUtil;
import com.nelani.blog_land_backend.Util.Validation.ModerationValidator;
import com.nelani.blog_land_backend.dto.PostDto;
import com.nelani.blog_land_backend.model.Category;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.CategoryRepository;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.response.PostResponse;
import com.nelani.blog_land_backend.service.impl.PostServiceImpl;

import jakarta.persistence.EntityManager;
import jakarta.validation.ValidationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

public class PostServiceImplTest {

        @Mock
        private UserRepository userRepository;

        @Mock
        private PostRepository postRepository;

        @Mock
        private CategoryRepository categoryRepository;

        @Mock
        private ModerationValidator moderationValidator;

        @Mock
        private JwtUtil jwtUtils;

        @Mock
        private EntityManager entityManager;

        @InjectMocks
        private PostServiceImpl postService;

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

                // Mock repository to return this user
                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(existingUser));

                // Set authenticated user in security context
                var auth = new UsernamePasswordAuthenticationToken(
                                existingUser, null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);

                Category category = new Category();
                category.setId(1L);
                category.setName("Tech");

                // Mock repository to return this user
                when(categoryRepository.findById(1L))
                                .thenReturn(Optional.of(category));
        }

        @Test
        void searchByKeyword_ShouldReturnPostsSortedByScore() {
                // Arrange
                String keyword = "spring";
                Pageable pageable = PageRequest.of(0, 5);

                Post post1 = new Post();
                post1.setId(1L);
                post1.setTitle("Spring Boot Guide");
                post1.setReadTime(10);
                post1.setSummary("This is a guide to Spring Boot.");
                post1.setContent("This is a guide to Spring Boot content.");

                Post post2 = new Post();
                post2.setId(2L);
                post2.setTitle("Intro to Java");
                post2.setReadTime(5);
                post2.setSummary("Java basics explained.");
                post2.setContent("Java basics explained content.");

                List<Post> postList = List.of(post1, post2);
                Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

                when(postRepository.searchByKeyword(eq(keyword), eq(pageable)))
                                .thenReturn(postPage);

                // Act
                List<PostResponse> results = postService.searchByKeyword(keyword);

                // Assert
                assertEquals(2, results.size());
                assertTrue(results.get(0).getScore() >= results.get(1).getScore());
                assertEquals(1L, results.get(0).getId());
                assertEquals("Spring Boot Guide", results.get(0).getTitle());
        }

        @Test
        void incrementViews_ShouldIncreaseViewCount_WhenPostExists() {
                // Arrange
                Long postId = 1L;
                Post post = new Post();
                post.setId(postId);
                post.setViewCount(5L);

                when(postRepository.findById(postId)).thenReturn(Optional.of(post));
                when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

                // Act
                postService.incrementViews(postId);

                // Assert
                assertEquals(6, post.getViewCount());
                verify(postRepository).save(post);
        }

        @Test
        void incrementViews_ShouldThrowException_WhenPostNotFount() {
                // Arrange
                Long postId = 1L;
                when(postRepository.findById(postId)).thenReturn(Optional.empty());

                // Act & Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class, () -> {
                                        postService.incrementViews(postId);
                                });

                // Assert
                assertEquals("Post not found", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(postRepository, never()).save(any());
        }

        @Test
        void addPost_ShouldSaveValidPost() {
                // Arrange
                PostDto postDto = new PostDto();
                postDto.setTitle("Spring Boot Guide");
                postDto.setSummary("This is a guide to Spring Boot.");
                postDto.setContent("This is a guide to Spring Boot content.");
                postDto.setImgUrl("local-picture");
                postDto.setCategoryId(1L);

                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).userModeration(any(User.class));

                // Act
                postService.addPost(postDto);

                // Assert
                ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
                ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
                verify(userRepository, times(1)).save(userCaptor.capture());
                verify(moderationValidator, times(1)).postModeration(postCaptor.capture());

                User savedUser = userCaptor.getValue();
                assertEquals(1, savedUser.getPosts().size());
                Post savedPost = savedUser.getPosts().get(0);
                assertEquals("Spring Boot Guide", savedPost.getTitle());
                assertEquals("This is a guide to Spring Boot.", savedPost.getSummary());
                assertEquals("This is a guide to Spring Boot content.", savedPost.getContent());
                assertEquals("local-picture", savedPost.getImgUrl());
        }

        @Test
        void addPost_ShouldThrowException_WhenPostExistsWithTheSameTile() {
                // Arrange
                PostDto postDto = new PostDto();
                postDto.setTitle("Spring Boot Guide");
                postDto.setSummary("This is a guide to Spring Boot.");
                postDto.setContent("This is a guide to Spring Boot content.");
                postDto.setImgUrl("local-picture");
                postDto.setCategoryId(1L);

                // Get the user from SecurityContext (set up in @BeforeEach)
                User authenticatedUser = (User) SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getPrincipal();

                // Add an existing post with the same title
                Post existingPost = new Post();
                existingPost.setId(1L);
                existingPost.setTitle("Spring Boot Guide");
                authenticatedUser.setPosts(new ArrayList<>(List.of(existingPost)));

                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).postModeration(any(Post.class));

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> postService.addPost(postDto));

                assertEquals("You cannot use the same title twice.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

        @Test
        void addPost_ShouldThrowException_WhenPostFailsModeration() {
                // Arrange
                PostDto postDto = new PostDto();
                postDto.setTitle("Spring Boot Guide");
                postDto.setSummary("This is a guide to Spring Boot.");
                postDto.setContent("fuck");
                postDto.setImgUrl("local-picture");
                postDto.setCategoryId(1L);

                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doThrow(new ValidationException("Post did not pass moderation."))
                                .when(moderationValidator).postModeration(any(Post.class));

                // Act + Assert
                ValidationException exception = assertThrows(
                                ValidationException.class,
                                () -> postService.addPost(postDto));

                assertEquals("Post did not pass moderation.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

        @Test
        void addPost_ShouldThrowException_WhenTitleIsEmpty() {
                // Arrange
                PostDto postDto = new PostDto();
                postDto.setTitle("    ");
                postDto.setSummary("This is a guide to Spring Boot.");
                postDto.setContent("This is a guide to Spring Boot content.");
                postDto.setImgUrl("local-picture");
                postDto.setCategoryId(1L);

                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).postModeration(any(Post.class));

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> postService.addPost(postDto));

                assertEquals("Title is required.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

        @Test
        void addPost_ShouldThrowException_WhenSummaryIsEmpty() {
                // Arrange
                PostDto postDto = new PostDto();
                postDto.setTitle("Spring Boot Guide");
                postDto.setSummary("    ");
                postDto.setContent("This is a guide to Spring Boot content.");
                postDto.setImgUrl("local-picture");
                postDto.setCategoryId(1L);

                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).postModeration(any(Post.class));

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> postService.addPost(postDto));

                assertEquals("Summary is required.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

        @Test
        void addPost_ShouldThrowException_WhenContentIsEmpty() {
                // Arrange
                PostDto postDto = new PostDto();
                postDto.setTitle("Spring Boot Guide");
                postDto.setSummary("This is a guide to Spring Boot.");
                postDto.setContent("    ");
                postDto.setImgUrl("local-picture");
                postDto.setCategoryId(1L);

                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).postModeration(any(Post.class));

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> postService.addPost(postDto));

                assertEquals("Content is required.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

        @Test
        void addPost_ShouldThrowException_WhenImgUrlIsEmpty() {
                // Arrange
                PostDto postDto = new PostDto();
                postDto.setTitle("Spring Boot Guide");
                postDto.setSummary("This is a guide to Spring Boot.");
                postDto.setContent("This is a guide to Spring Boot content.");
                postDto.setImgUrl("   ");
                postDto.setCategoryId(1L);

                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).postModeration(any(Post.class));

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> postService.addPost(postDto));

                assertEquals("Img Url is required.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

        @Test
        void addPost_ShouldThrowException_WhenCategoryDoesNotExist() {
                // Arrange
                PostDto postDto = new PostDto();
                postDto.setTitle("Spring Boot Guide");
                postDto.setSummary("This is a guide to Spring Boot.");
                postDto.setContent("This is a guide to Spring Boot content.");
                postDto.setImgUrl("local-picture");
                postDto.setCategoryId(2L);

                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).postModeration(any(Post.class));

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> postService.addPost(postDto));

                assertEquals("Category does not exist.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

        @Test
        void updatePost_ShouldSaveValidPostUpdate() {
                // Arrange
                PostDto postDto = new PostDto();
                postDto.setId(1L);
                postDto.setTitle("Spring Boot Guide");
                postDto.setSummary("This is a guide to Spring Boot.");
                postDto.setContent("This is a guide to Spring Boot content.");
                postDto.setImgUrl("local-picture");
                postDto.setCategoryId(1L);
                postDto.setDraft(true);
                postDto.setReferences("url1, url2");

                // Get the user from SecurityContext (set up in @BeforeEach)
                User authenticatedUser = (User) SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getPrincipal();

                Post existingPost = new Post();
                existingPost.setId(1L);
                existingPost.setTitle("Spring Boot Guide");
                existingPost.setUser(authenticatedUser);
                authenticatedUser.setPosts(new ArrayList<>(List.of(existingPost)));

                Category category = new Category();
                category.setId(1L);
                category.setName("Tech");

                when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));
                doNothing().when(moderationValidator).userModeration(any(User.class));

                // Act
                postService.updatePost(postDto);

                // Assert post save
                ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
                verify(postRepository, times(1)).save(postCaptor.capture());
                Post savedPost = postCaptor.getValue();

                assertEquals("Spring Boot Guide", savedPost.getTitle());
                assertEquals("This is a guide to Spring Boot.", savedPost.getSummary());
                assertEquals("This is a guide to Spring Boot content.", savedPost.getContent());
                assertEquals("local-picture", savedPost.getImgUrl());
                assertTrue(savedPost.isDraft());
                assertEquals("url1, url2", savedPost.getReferences());
                verify(moderationValidator).postModeration(savedPost);
        }

        @Test
        void updatePost_ShouldThrowException_WhenPostDoesNotExist() {
                // Arrange
                PostDto postDto = new PostDto();
                postDto.setId(1L);
                postDto.setTitle("Spring Boot Guide");
                postDto.setSummary("This is a guide to Spring Boot.");
                postDto.setContent("This is a guide to Spring Boot content.");
                postDto.setImgUrl("local-picture");
                postDto.setCategoryId(1L);
                postDto.setDraft(true);
                postDto.setReferences("url1, url2");

                Category category = new Category();
                category.setId(1L);
                category.setName("Tech");
                when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).userModeration(any(User.class));

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> postService.updatePost(postDto));

                assertEquals("Post does not exist.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(postRepository, never()).save(any());
        }

        @Test
        void updatePost_ShouldThrowException_WhenPostFailsModeration() {
                // Arrange
                PostDto postDto = new PostDto();
                postDto.setId(1L);
                postDto.setTitle("Spring Boot Guide");
                postDto.setSummary("fuck");
                postDto.setContent("This is a guide to Spring Boot content.");
                postDto.setImgUrl("local-picture");
                postDto.setCategoryId(1L);
                postDto.setDraft(true);
                postDto.setReferences("url1, url2");

                // Get the user from SecurityContext (set up in @BeforeEach)
                User authenticatedUser = (User) SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getPrincipal();

                Post existingPost = new Post();
                existingPost.setId(1L);
                existingPost.setTitle("Spring Boot Guide");
                existingPost.setUser(authenticatedUser);
                authenticatedUser.setPosts(new ArrayList<>(List.of(existingPost)));

                Category category = new Category();
                category.setId(1L);
                category.setName("Tech");
                when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));
                doThrow(new ValidationException("Post did not pass moderation."))
                                .when(moderationValidator).postModeration(any(Post.class));

                // Act + Assert
                ValidationException exception = assertThrows(
                                ValidationException.class,
                                () -> postService.updatePost(postDto));

                assertEquals("Post did not pass moderation.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(postRepository, never()).save(any());
        }

        @Test
        void updatePost_ShouldThrowException_WhenPostIdIsNull() {
                // Arrange
                PostDto postDto = new PostDto();
                postDto.setId(null);
                postDto.setTitle("Spring Boot Guide");
                postDto.setSummary("This is a guide to Spring Boot.");
                postDto.setContent("This is a guide to Spring Boot content.");
                postDto.setImgUrl("local-picture");
                postDto.setCategoryId(1L);
                postDto.setDraft(true);
                postDto.setReferences("url1, url2");

                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");

                doNothing().when(moderationValidator).userModeration(any(User.class));

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> postService.updatePost(postDto));

                assertEquals("Post Id is required.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(postRepository, never()).save(any());
        }

        @Test
        void updatePost_ShouldThrowException_WhenPostTitleIsEmpty() {
                // Arrange
                PostDto postDto = new PostDto();
                postDto.setId(1L);
                postDto.setTitle("    ");
                postDto.setSummary("This is a guide to Spring Boot.");
                postDto.setContent("This is a guide to Spring Boot content.");
                postDto.setImgUrl("local-picture");
                postDto.setCategoryId(1L);
                postDto.setDraft(true);
                postDto.setReferences("url1, url2");

                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).userModeration(any(User.class));

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> postService.updatePost(postDto));

                assertEquals("Title is required.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(postRepository, never()).save(any());
        }

        @Test
        void updatePost_ShouldThrowException_WhenPostSummaryIsEmpty() {
                // Arrange
                PostDto postDto = new PostDto();
                postDto.setId(1L);
                postDto.setTitle("Spring Boot Guide");
                postDto.setSummary("    ");
                postDto.setContent("This is a guide to Spring Boot content.");
                postDto.setImgUrl("local-picture");
                postDto.setCategoryId(1L);
                postDto.setDraft(true);
                postDto.setReferences("url1, url2");

                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).userModeration(any(User.class));

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> postService.updatePost(postDto));

                assertEquals("Summary is required.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(postRepository, never()).save(any());
        }

        @Test
        void updatePost_ShouldThrowException_WhenPostContentIsEmpty() {
                // Arrange
                PostDto postDto = new PostDto();
                postDto.setId(1L);
                postDto.setTitle("Spring Boot Guide");
                postDto.setSummary("This is a guide to Spring Boot");
                postDto.setContent("        ");
                postDto.setImgUrl("local-picture");
                postDto.setCategoryId(1L);
                postDto.setDraft(true);
                postDto.setReferences("url1, url2");

                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).userModeration(any(User.class));

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> postService.updatePost(postDto));

                assertEquals("Content is required.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(postRepository, never()).save(any());
        }

        @Test
        void updatePost_ShouldThrowException_WhenPostImgUrlIsEmpty() {
                // Arrange
                PostDto postDto = new PostDto();
                postDto.setId(1L);
                postDto.setTitle("Spring Boot Guide");
                postDto.setSummary("This is a guide to Spring Boot");
                postDto.setContent("This is a guide to Spring Boot content.");
                postDto.setImgUrl("   ");
                postDto.setCategoryId(1L);
                postDto.setDraft(true);
                postDto.setReferences("url1, url2");

                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).userModeration(any(User.class));

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> postService.updatePost(postDto));

                assertEquals("Img Url is required.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(postRepository, never()).save(any());
        }

        @Test
        void updatePost_ShouldThrowException_WhenPostCategoryIsEmpty() {
                // Arrange
                PostDto postDto = new PostDto();
                postDto.setId(1L);
                postDto.setTitle("Spring Boot Guide");
                postDto.setSummary("This is a guide to Spring Boot");
                postDto.setContent("This is a guide to Spring Boot content.");
                postDto.setImgUrl("local-picture");
                postDto.setCategoryId(null);
                postDto.setDraft(true);
                postDto.setReferences("url1, url2");

                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).userModeration(any(User.class));

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> postService.updatePost(postDto));

                assertEquals("Category Id is required.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(postRepository, never()).save(any());
        }

        @Test
        void deletePost_ShouldDeletePost() {
                Long postId = 1L;

                // Get the user from SecurityContext (set up in @BeforeEach)
                User authenticatedUser = (User) SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getPrincipal();

                Post existingPost = new Post();
                existingPost.setId(1L);
                existingPost.setTitle("Spring Boot Guide");
                existingPost.setUser(authenticatedUser);
                authenticatedUser.getPosts().add(existingPost);

                when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));
                doNothing().when(entityManager).flush();
                doNothing().when(entityManager).clear();

                // Act
                postService.deletePost(postId);

                // Assert
                verify(entityManager, times(1)).flush();
                verify(entityManager, times(1)).clear();
                assertFalse(authenticatedUser.getPosts().contains(existingPost));
        }

        @Test
        void deletePost_ShouldThrowException_WhenNoPostExists() {
                // Arrange
                Long postId = 1L;

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> postService.deletePost(postId));

                assertEquals("Post does not exist.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(postRepository, never()).delete(any());
        }

}
