package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.cache.LikeCacheHelper;
import com.nelani.blog_land_backend.model.*;
import com.nelani.blog_land_backend.repository.LikeRepository;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.response.LikeResponse;
import com.nelani.blog_land_backend.service.impl.LikeServiceImpl;
import com.nelani.blog_land_backend.sockets.LikesSocket;
import com.nelani.blog_land_backend.util.validation.UserValidation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class LikeServiceImplTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private LikesSocket likesSocket;

    @Mock
    private LikeCacheHelper likeCacheHelper;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserValidation userValidation;

    @InjectMocks
    private LikeServiceImpl likeService;

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
                .build();

        post = Post.builder()
                .id(UUID.randomUUID())
                .title("Sample Post")
                .build();
    }

    @Test
    public void LikeServiceImplTest_GetPostLikesCount_ReturnsLikeCount() {
        // Arrange
        UUID postId = UUID.randomUUID();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.countByPost(post)).thenReturn(15L);

        // Act
        long likeCount = likeService.getPostLikesCount(postId);

        // Assert
        Assertions.assertThat(likeCount).isEqualTo(15L);

        verify(postRepository, times(1)).findById(postId);
        verify(likeRepository, times(1)).countByPost(post);
    }

    @Test
    public void LikeServiceImplTest_GetUserLikes_ReturnsListOfLikes() {
        // Arrange
        UUID postId1 = UUID.randomUUID();
        UUID postId2 = UUID.randomUUID();

        Post post1 = Post.builder().id(postId1).title("Post 1").build();
        Post post2 = Post.builder().id(postId2).title("Post 2").build();

        UUID likeId1 = UUID.randomUUID();
        UUID likeId2 = UUID.randomUUID();

        Like like1 = Like.builder().id(likeId1).likedAt(LocalDateTime.now().minusDays(1)).post(post1).user(user)
                .build();
        Like like2 = Like.builder().id(likeId2).likedAt(LocalDateTime.now()).post(post2).user(user).build();

        List<Like> userLikes = List.of(like1, like2);

        when(userValidation.getAuthenticatedUser()).thenReturn(user);
        when(likeRepository.findAllByUser(user)).thenReturn(userLikes);

        // Act
        List<LikeResponse> response = likeService.getUserLikes();

        // Assert
        Assertions.assertThat(response).hasSize(2);
        Assertions.assertThat(response)
                .extracting(LikeResponse::likeId)
                .containsExactlyInAnyOrder(likeId1, likeId2);
        Assertions.assertThat(response)
                .extracting(LikeResponse::postId)
                .containsExactlyInAnyOrder(postId1, postId2);

        verify(userValidation, times(1)).getAuthenticatedUser();
        verify(likeRepository, times(1)).findAllByUser(user);
    }

    @Test
    public void LikeServiceImplTest_AddLike_SavesSuccessfully() {
        // Arrange
        UUID postId = UUID.randomUUID();
        Post post = Post.builder().id(postId).title("Test Post").build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userValidation.getAuthenticatedUser()).thenReturn(user);
        when(likeRepository.findByUserAndPost(user, post)).thenReturn(Optional.empty());

        Like savedLike = Like.builder().id(UUID.randomUUID()).post(post).user(user).likedAt(LocalDateTime.now())
                .build();
        when(likeRepository.save(any(Like.class))).thenReturn(savedLike);

        List<Like> userLikes = List.of(savedLike);
        when(likeRepository.findAllByUser(user)).thenReturn(userLikes);
        when(likeRepository.countByPost(post)).thenReturn(1L);

        // Act
        likeService.addLike(postId);

        // Assert
        verify(postRepository, times(1)).findById(postId);
        verify(userValidation, times(1)).getAuthenticatedUser();
        verify(likeRepository, times(1)).findByUserAndPost(user, post);
        verify(likeRepository, times(1)).save(any(Like.class));
        verify(likeRepository, times(1)).findAllByUser(user);
        verify(likeRepository, times(1)).countByPost(post);

        verify(likesSocket, times(1)).updatePostLikes(1L, postId);
        verify(likesSocket, times(1)).updateUserLikes(any(), anyList());
        verify(likeCacheHelper, times(1)).evictAllForPost(user.getEmail(), postId);
    }

    @Test
    public void LikeServiceImplTest_RemoveLike_Success() {
        // Arrange
        UUID likeId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();

        Post post = Post.builder().id(postId).title("Test Post").build();
        Like like = Like.builder().id(likeId).post(post).user(user).likedAt(LocalDateTime.now()).build();

        when(likeRepository.findById(likeId)).thenReturn(Optional.of(like));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userValidation.getAuthenticatedUser()).thenReturn(user);

        List<Like> userLikes = List.of();
        when(likeRepository.findAllByUser(user)).thenReturn(userLikes);
        when(likeRepository.countByPost(post)).thenReturn(0L);

        // Act
        likeService.removeLike(likeId);

        // Assert
        verify(likeRepository, times(1)).findById(likeId);
        verify(postRepository, times(1)).findById(postId);
        verify(userValidation, times(1)).getAuthenticatedUser();
        verify(likeRepository, times(1)).delete(like);
        verify(likeRepository, times(1)).findAllByUser(user);
        verify(likeRepository, times(1)).countByPost(post);
        verify(likesSocket, times(1)).updatePostLikes(0L, postId);
        verify(likesSocket, times(1)).updateUserLikes(any(), anyList());
        verify(likeCacheHelper, times(1)).evictAllForPost(user.getEmail(), postId);
    }

}
