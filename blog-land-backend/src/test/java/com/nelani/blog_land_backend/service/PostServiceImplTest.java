package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.cache.PostCacheHelper;
import com.nelani.blog_land_backend.model.*;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.response.PostResponse;
import com.nelani.blog_land_backend.service.impl.PostServiceImpl;
import com.nelani.blog_land_backend.sockets.PostSocket;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class PostServiceImplTest {

    @Mock
    private PostCacheHelper postCacheHelper;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostSocket postSocket;

    @InjectMocks
    private PostServiceImpl postService;

    private List<Post> postList;

    @BeforeEach
    public void init() {
        User user = User.builder()
                .firstname("firstname")
                .lastname("lastname")
                .email("test-email@test.co.za")
                .provider(Provider.LOCAL)
                .password("Password@123")
                .build();

        Category category = Category.builder()
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
    public void PostServiceImplTest_SearchByKeyword_ReturnsVoid() {
        // Arrange
        Page<Post> page = new PageImpl<>(postList);
        when(postRepository.searchByKeyword(eq("Title"), any(Pageable.class))).thenReturn(page);

        // Act
        var result = postService.searchByKeyword("Title");

        // Assert
        Assertions.assertThat(result).hasSize(5);
        Assertions.assertThat(result)
                .extracting(PostResponse::title)
                .containsExactlyInAnyOrder("Title", "Title1", "Title2", "Title3", "Title4");

        verify(postRepository, times(1)).searchByKeyword(eq("Title"), any(Pageable.class));
    }

    @Test
    public void PostServiceImplTest_IncrementViews_ReturnsVoid() {
        // Arrange
        when(postRepository.findById(any())).thenReturn(Optional.of(postList.get(0)));

        // Act
        postService.incrementViews(postList.get(0).getId());

        // Assert
        verify(postRepository, times(1)).save(postList.get(0));
    }

    private Post createPost(String title, User user, Category category) {
        Post post = Post.builder()
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
