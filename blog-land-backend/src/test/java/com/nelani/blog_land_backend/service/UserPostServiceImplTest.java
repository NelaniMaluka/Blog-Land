package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.cache.PostCacheHelper;
import com.nelani.blog_land_backend.dto.PostDto;
import com.nelani.blog_land_backend.model.Category;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.CategoryRepository;
import com.nelani.blog_land_backend.repository.CommentRepository;
import com.nelani.blog_land_backend.repository.LikeRepository;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.response.PostResponse;
import com.nelani.blog_land_backend.service.impl.UserPostServiceImpl;
import com.nelani.blog_land_backend.sockets.PostSocket;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserPostServiceImplTest {

        @Mock
        private PostRepository postRepository;

        @Mock
        private CategoryRepository categoryRepository;

        @Mock
        private ModerationValidator moderationValidator;

        @Mock
        private PostCacheHelper postCacheHelper;

        @Mock
        private PostSocket postSocket;

        @Mock
        private UserValidation userValidation;

        @Mock
        private CommentRepository commentRepository;

        @Mock
        private LikeRepository likeRepository;

        @InjectMocks
        private UserPostServiceImpl userPostService;

        private User user;
        private List<Post> postList;
        private Category category;

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

                category = Category.builder()
                                .id(UUID.randomUUID())
                                .name("testCategory")
                                .build();

                postList = new ArrayList<>();
                postList.add(createPost("Title", user, category));
                postList.add(createPost("Title1", user, category));
                postList.add(createPost("Title2", user, category));
                postList.add(createPost("Title3", user, category));
                postList.add(createPost("Title4", user, category));
        }

        @Test
        public void UserPostServiceImplTest_GetByUserId_ReturnsPagedPosts() {
                // Arrange
                int page = 0;
                int size = 3;
                Pageable pageable = PageRequest.of(page, size);

                Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());

                when(userValidation.getAuthenticatedUser()).thenReturn(user);
                when(postRepository.findByUserIdOrderByCreatedAtDesc(eq(user.getId()), eq(pageable)))
                                .thenReturn(postPage);

                // Act
                Page<PostResponse> result = userPostService.getByUserId(page, size);

                // Assert
                Assertions.assertThat(result).isNotNull();
                Assertions.assertThat(result.getContent()).hasSize(5);
                Assertions.assertThat(result.getContent())
                                .extracting(PostResponse::title)
                                .containsExactlyInAnyOrder("Title", "Title1", "Title2", "Title3", "Title4");

                verify(userValidation, times(1)).getAuthenticatedUser();
                verify(postRepository, times(1))
                                .findByUserIdOrderByCreatedAtDesc(eq(user.getId()), eq(pageable));
        }

        @Test
        public void UserPostServiceImplTest_AddPost_SavesSuccessfully() {
                // Arrange
                PostDto postDto = PostDto.builder()
                                .title("New Post")
                                .categoryId(category.getId())
                                .imgUrl("image.jpg")
                                .references("Some references")
                                .summary("A short summary")
                                .draft(false)
                                .scheduledAt(LocalDateTime.now().plusDays(1))
                                .content("Post content")
                                .build();

                when(userValidation.getAuthenticatedUser()).thenReturn(user);
                when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
                when(postRepository.findAllByUserId(user.getId())).thenReturn(List.of());

                // Act
                userPostService.addPost(postDto);

                // Assert
                verify(userValidation, times(1)).getAuthenticatedUser();
                verify(categoryRepository, times(1)).findById(category.getId());
                verify(postRepository, times(1)).save(any(Post.class));
                verify(moderationValidator, times(1)).postModeration(any(Post.class));
                verify(postCacheHelper, times(1)).evictAllUserPosts(eq(user.getEmail()), any(),
                                eq(postDto.categoryId()));
                verify(postSocket, times(1)).addNewPost(any(Post.class));
        }

        @Test
        public void UserPostServiceImplTest_UpdatePost_UpdatesSuccessfully() {
                // Arrange
                Category oldCategory = Category.builder()
                                .id(UUID.randomUUID())
                                .name("Old Tech")
                                .build();

                Post existingPost = Post.builder()
                                .id(UUID.randomUUID())
                                .title("Old Post Title")
                                .content("Old Content")
                                .user(user)
                                .category(oldCategory)
                                .build();

                PostDto postDto = PostDto.builder()
                                .id(existingPost.getId())
                                .title("Updated Title")
                                .content("Updated content here")
                                .imgUrl("updated.jpg")
                                .references("updated reference")
                                .summary("updated summary")
                                .categoryId(category.getId())
                                .draft(false)
                                .scheduledAt(LocalDateTime.now().plusDays(2))
                                .updatedAt(LocalDateTime.now())
                                .build();

                when(userValidation.getAuthenticatedUser()).thenReturn(user);
                when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
                when(postRepository.findById(existingPost.getId())).thenReturn(Optional.of(existingPost));

                // Act
                userPostService.updatePost(postDto);

                // Assert
                verify(userValidation, times(1)).getAuthenticatedUser();
                verify(categoryRepository, times(1)).findById(category.getId());
                verify(postRepository, times(1)).findById(existingPost.getId());
                verify(moderationValidator, times(1)).postModeration(existingPost);
                verify(postRepository, times(1)).save(existingPost);
                verify(postCacheHelper, times(1)).evictAllUserPosts(eq(user.getEmail()), eq(existingPost.getId()),
                                eq(postDto.categoryId()));
                verify(postSocket, times(1)).updatePost(existingPost);

                Assertions.assertThat(existingPost.getTitle()).isEqualTo("Updated Title");
                Assertions.assertThat(existingPost.getCategory().getId()).isEqualTo(category.getId());
        }

        @Test
        public void UserPostServiceImplTest_DeletePost_DeletesSuccessfully() {
                // Arrange
                UUID postId = UUID.randomUUID();

                Post existingPost = Post.builder()
                                .id(postId)
                                .title("Sample Post")
                                .user(user)
                                .category(category)
                                .build();

                when(userValidation.getAuthenticatedUser()).thenReturn(user);
                when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

                // Act
                userPostService.deletePost(postId);

                // Assert
                verify(userValidation, times(1)).getAuthenticatedUser();
                verify(postRepository, times(1)).findById(postId);
                verify(likeRepository, times(1)).deleteByPost(existingPost);
                verify(commentRepository, times(1)).deleteByPost(existingPost);
                verify(postRepository, times(1)).delete(existingPost);
                verify(postCacheHelper, times(1))
                                .evictAllUserPosts(eq(user.getEmail()), eq(existingPost.getId()), eq(category.getId()));
                verify(postSocket, times(1)).deletePost(postId);
        }

        private Post createPost(String title, User user, Category category) {
                Post post = Post.builder()
                                .id(UUID.randomUUID())
                                .title(title)
                                .summary("summary")
                                .imgUrl("imgUrl")
                                .user(user)
                                .category(category)
                                .build();
                post.setContent("This is some example content for the blog post.");
                return post;
        }
}
