package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.model.Category;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.CategoryRepository;
import com.nelani.blog_land_backend.response.PostResponse;
import com.nelani.blog_land_backend.service.impl.CategoryServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CategoryServiceImplTest {

        @Mock
        private CategoryRepository categoryRepository;

        @InjectMocks
        private CategoryServiceImpl categoryService;

        @Test
        public void CategoryServiceImplTest_GetByCategoryId_ReturnsPagedPosts() {
                // Arrange
                UUID categoryId = UUID.randomUUID();
                int page = 0;
                int size = 3;
                String order = "latest";

                Category category = Category.builder()
                                .id(categoryId)
                                .name("Tech")
                                .build();

                User user = User.builder()
                                .id(UUID.randomUUID())
                                .firstname("John")
                                .lastname("Doe")
                                .email("test@test.com")
                                .build();

                Post post1 = Post.builder().id(UUID.randomUUID()).title("Post 1").user(user).category(category).build();
                Post post2 = Post.builder().id(UUID.randomUUID()).title("Post 2").user(user).category(category).build();
                Post post3 = Post.builder().id(UUID.randomUUID()).title("Post 3").user(user).category(category).build();

                List<Post> posts = List.of(post1, post2, post3);
                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
                Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());

                when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
                when(categoryRepository.findByCategoryId(categoryId, pageable)).thenReturn(postPage);

                // Act
                Page<PostResponse> result = categoryService.getByCategoryId(categoryId, page, size, order);

                // Assert
                Assertions.assertThat(result).isNotNull();
                Assertions.assertThat(result.getContent()).hasSize(3);
                Assertions.assertThat(result.getContent())
                                .extracting(PostResponse::title)
                                .containsExactlyInAnyOrder("Post 1", "Post 2", "Post 3");

                verify(categoryRepository, times(1)).findById(categoryId);
                verify(categoryRepository, times(1)).findByCategoryId(categoryId, pageable);
        }

}
