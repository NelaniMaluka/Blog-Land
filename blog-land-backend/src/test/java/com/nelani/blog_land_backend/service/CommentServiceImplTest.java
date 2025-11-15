package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.cache.CommentCacheHelper;
import com.nelani.blog_land_backend.dto.CommentDto;
import com.nelani.blog_land_backend.model.Comment;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.CommentRepository;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.response.CommentResponse;
import com.nelani.blog_land_backend.service.impl.CommentServiceImpl;
import com.nelani.blog_land_backend.sockets.CommentSocket;
import com.nelani.blog_land_backend.util.validation.ModerationValidator;
import com.nelani.blog_land_backend.util.validation.UserValidation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CommentServiceImplTest {

        @Mock
        private CommentCacheHelper commentCacheHelper;

        @Mock
        private CommentRepository commentRepository;

        @Mock
        private ModerationValidator moderationValidator;

        @Mock
        private CommentSocket commentSocket;

        @Mock
        private PostRepository postRepository;

        @Mock
        private UserValidation userValidation;

        @InjectMocks
        private CommentServiceImpl commentService;

        private User user;
        private Post post;

        @BeforeEach
        public void init() {
                user = User.builder()
                                .id(UUID.randomUUID())
                                .firstname("firstname")
                                .lastname("lastname")
                                .email("test-email@test.co.za")
                                .provider(Provider.LOCAL)
                                .password("Password@123")
                                .naniId("naniId")
                                .build();

                post = Post.builder()
                                .id(UUID.randomUUID())
                                .title("Sample Post")
                                .build();
        }

        @Test
        public void CommentServiceImplTest_GetPostLikesCount_ReturnsCount() {
                // Arrange
                UUID postId = UUID.randomUUID();

                when(postRepository.findById(postId)).thenReturn(Optional.of(post));
                when(commentRepository.countByPost(post)).thenReturn(7L);

                // Act
                long count = commentService.getCountByPostId(postId);

                // Assert
                Assertions.assertThat(count).isEqualTo(7L);
                verify(postRepository, times(1)).findById(postId);
                verify(commentRepository, times(1)).countByPost(post);
        }

        @Test
        public void CommentServiceImplTest_GetByPostId_ReturnsPagedComments() {
                // Arrange
                UUID postId = UUID.randomUUID();
                int page = 0;
                int size = 3;
                Pageable pageable = PageRequest.of(page, size);

                // Mock post existence
                when(postRepository.findById(postId)).thenReturn(Optional.of(post));

                // Create some comments
                Comment comment1 = Comment.builder().id(UUID.randomUUID()).content("First")
                                .createdAt(LocalDateTime.now().minusHours(1)).user(user).build();
                Comment comment2 = Comment.builder().id(UUID.randomUUID()).content("Second")
                                .createdAt(LocalDateTime.now()).user(user).build();
                Comment comment3 = Comment.builder().id(UUID.randomUUID()).content("Third")
                                .createdAt(LocalDateTime.now().minusHours(2)).user(user).build();

                List<Comment> comments = List.of(comment1, comment2, comment3);
                Page<Comment> commentPage = new PageImpl<>(comments, pageable, comments.size());

                when(commentRepository.findByPostId(postId, pageable)).thenReturn(commentPage);

                // Act
                Page<CommentResponse> result = commentService.getByPostId(postId, page, size);

                // Assert
                Assertions.assertThat(result).isNotNull();
                Assertions.assertThat(result.getContent()).hasSize(3);

                // The newest comment should come first
                Assertions.assertThat(result.getContent().get(0).content()).isEqualTo("Second");
                Assertions.assertThat(result.getContent().get(1).content()).isEqualTo("First");
                Assertions.assertThat(result.getContent().get(2).content()).isEqualTo("Third");

                verify(postRepository, times(1)).findById(postId);
                verify(commentRepository, times(1)).findByPostId(postId, pageable);
        }

        @Test
        public void CommentServiceImplTest_GetByUserId_ReturnsCommentIds() {
                // Arrange
                UUID postId = UUID.randomUUID();
                UUID commentId1 = UUID.randomUUID();
                UUID commentId2 = UUID.randomUUID();

                // Mock authenticated user
                User user = User.builder().id(UUID.randomUUID()).firstname("John").build();
                when(userValidation.getAuthenticatedUser()).thenReturn(user);

                // Mock repository to return comments
                Comment comment1 = Comment.builder().id(commentId1).content("First comment").user(user).build();
                Comment comment2 = Comment.builder().id(commentId2).content("Second comment").user(user).build();
                List<Comment> comments = List.of(comment1, comment2);
                when(commentRepository.findByUserIdAndPostId(user.getId(), postId)).thenReturn(comments);

                // Act
                List<UUID> result = commentService.getByUserId(postId);

                // Assert
                Assertions.assertThat(result).isNotNull();
                Assertions.assertThat(result).containsExactlyInAnyOrder(commentId1, commentId2);

                verify(userValidation, times(1)).getAuthenticatedUser();
                verify(commentRepository, times(1)).findByUserIdAndPostId(user.getId(), postId);
        }

        @Test
        public void CommentServiceImplTest_AddComment_SavesSuccessfully() {
                // Arrange
                UUID postId = UUID.randomUUID();

                // Mock authenticated user
                User user = User.builder()
                                .id(UUID.randomUUID())
                                .firstname("John")
                                .lastname("Doe")
                                .email("test@test.com")
                                .build();

                CommentDto commentDto = CommentDto.builder()
                                .content("This is a test comment")
                                .build();

                when(userValidation.getAuthenticatedUser()).thenReturn(user);
                when(postRepository.findById(postId)).thenReturn(Optional.of(post));

                // Act
                commentService.addComment(postId, commentDto);

                // Assert
                verify(userValidation, times(1)).getAuthenticatedUser();
                verify(postRepository, times(1)).findById(postId);
                verify(moderationValidator, times(1)).commentModeration(any(Comment.class));
                verify(commentRepository, times(1)).save(any(Comment.class));
                verify(commentSocket, times(1)).updateCommentCount(post);
                verify(commentSocket, times(1)).addNewComments(eq(post), any(Comment.class));
                verify(commentSocket, times(1)).addUserComment(eq(user), any(Comment.class), eq(post));
                verify(commentCacheHelper, times(1)).evictAllForPost(user.getEmail(), postId);
        }

        @Test
        public void CommentServiceImplTest_UpdateComment_UpdatesSuccessfully() {
                // Arrange
                UUID commentId = UUID.randomUUID();

                User user = User.builder()
                                .id(UUID.randomUUID())
                                .firstname("John")
                                .lastname("Doe")
                                .email("test@test.com")
                                .build();

                Comment existingComment = Comment.builder()
                                .id(commentId)
                                .content("Old content")
                                .user(user)
                                .post(post)
                                .build();

                CommentDto commentDto = CommentDto.builder()
                                .content("Updated content")
                                .build();

                when(userValidation.getAuthenticatedUser()).thenReturn(user);
                when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
                when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

                // Act
                commentService.updateComment(post.getId(), commentId, commentDto);

                // Assert
                verify(userValidation, times(1)).getAuthenticatedUser();
                verify(postRepository, times(1)).findById(post.getId());
                verify(commentRepository, times(1)).findById(commentId);
                verify(moderationValidator, times(1)).commentModeration(existingComment);
                verify(commentRepository, times(1)).save(existingComment);
                verify(commentSocket, times(1)).updateComment(post, existingComment);
                verify(commentCacheHelper, times(1)).evictPostCommentsPaginated();

                Assertions.assertThat(existingComment.getContent()).isEqualTo("Updated content");
        }

        @Test
        public void CommentServiceImplTest_DeleteComment_DeletesSuccessfully() {
                // Arrange
                UUID commentId = UUID.randomUUID();

                User user = User.builder()
                                .id(UUID.randomUUID())
                                .firstname("John")
                                .lastname("Doe")
                                .email("test@test.com")
                                .build();

                Comment comment = Comment.builder()
                                .id(commentId)
                                .content("Some comment")
                                .user(user)
                                .post(post)
                                .build();

                when(userValidation.getAuthenticatedUser()).thenReturn(user);
                when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

                // Act
                commentService.deleteComment(commentId);

                // Assert
                verify(userValidation, times(1)).getAuthenticatedUser();
                verify(commentRepository, times(1)).findById(commentId);
                verify(commentRepository, times(1)).delete(comment);
                verify(commentSocket, times(1)).updateCommentCount(post);
                verify(commentSocket, times(1)).deleteComment(post, commentId);
                verify(commentSocket, times(1)).removeUserComment(user, comment, post);
                verify(commentCacheHelper, times(1)).evictAllForPost(user.getEmail(), post.getId());
        }

}
