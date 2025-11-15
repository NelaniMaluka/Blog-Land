package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.mapper.PostBuilder;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.model.Category;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.response.PostResponse;
import com.nelani.blog_land_backend.security.JwtService;
import com.nelani.blog_land_backend.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PostsController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class PostsControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private PostRepository postRepository;

        @MockitoBean
        private PostService postService;

        @MockitoBean
        private JwtService jwtService;

        @MockitoBean
        private PostBuilder postBuilder;

        @MockitoBean
        private UserDetailsService userDetailsService;

        private User user;

        @BeforeEach
        public void init() {
                user = User.builder()
                                .firstname("firstname")
                                .lastname("lastname")
                                .email("test-email@test.co.za")
                                .provider(Provider.LOCAL)
                                .build();
        }

        @Test
        void PostsControllerTest_SearchPosts_ShouldReturnOk() throws Exception {
                // Arrange
                String keyword = "Spring";
                PostResponse post1 = PostResponse.builder()
                                .id(UUID.randomUUID())
                                .title("Spring Boot Tutorial")
                                .content("Learn Spring Boot")
                                .build();

                PostResponse post2 = PostResponse.builder()
                                .id(UUID.randomUUID())
                                .title("Advanced Spring")
                                .content("Deep dive into Spring")
                                .build();

                List<PostResponse> results = List.of(post1, post2);
                when(postService.searchByKeyword(keyword)).thenReturn(results);

                // Act & Assert
                mockMvc.perform(get("/api/public/posts/search")
                                .param("keyword", keyword))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].title").value("Spring Boot Tutorial"))
                                .andExpect(jsonPath("$[1].title").value("Advanced Spring"));

                verify(postService, times(1)).searchByKeyword(keyword);
        }

        @Test
        void PostsControllerTest_GetRandomPost_ShouldReturnOk() throws Exception {
                // Arrange
                Category category = new Category();
                category.setId(UUID.randomUUID());
                category.setName("Tech");

                Post post = new Post();
                post.setId(UUID.randomUUID());
                post.setTitle("Random Post");
                post.setContent("Random content");
                post.setCategory(category);
                post.setUser(new User());

                when(postRepository.findRandomPost()).thenReturn(post);

                // Act & Assert
                mockMvc.perform(get("/api/public/posts/random"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.title").value(post.getTitle()))
                                .andExpect(jsonPath("$.categoryId").value(category.getId().toString()));

                verify(postRepository, times(1)).findRandomPost();
        }

        @Test
        void PostsControllerTest_GetLatestPost_ShouldReturnOk() throws Exception {
                // Arrange
                PostResponse post1 = PostResponse.builder()
                                .id(UUID.randomUUID())
                                .title("Latest Post 1")
                                .content("Content 1")
                                .build();

                PostResponse post2 = PostResponse.builder()
                                .id(UUID.randomUUID())
                                .title("Latest Post 2")
                                .content("Content 2")
                                .build();

                List<PostResponse> posts = List.of(post1, post2);
                when(postService.getLatestPost(0, 2)).thenReturn(posts);

                // Act & Assert
                mockMvc.perform(get("/api/public/posts/latest")
                                .param("page", "0")
                                .param("size", "2"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].title").value("Latest Post 1"))
                                .andExpect(jsonPath("$[1].title").value("Latest Post 2"));

                verify(postService, times(1)).getLatestPost(0, 2);
        }

        @Test
        void PostsControllerTest_GetTrendingPost_ShouldReturnOk() throws Exception {
                // Arrange
                int page = 0;
                int size = 2;

                Post post1 = new Post();
                post1.setId(UUID.randomUUID());
                post1.setTitle("Popular Post 1");
                post1.setContent("Content 1");

                Post post2 = new Post();
                post2.setId(UUID.randomUUID());
                post2.setTitle("Popular Post 2");
                post2.setContent("Content 2");

                Category category = mock(Category.class);
                when(category.getId()).thenReturn(UUID.randomUUID());
                post1.setCategory(category);
                post2.setCategory(category);
                post1.setUser(user);
                post2.setUser(user);

                Page<Post> popularPosts = new PageImpl<>(List.of(post1, post2));
                when(postRepository.findTrendingPosts(PageRequest.of(page, size))).thenReturn(popularPosts);

                // Act & Assert
                mockMvc.perform(get("/api/public/posts/popular")
                                .param("page", String.valueOf(page))
                                .param("size", String.valueOf(size)))
                                .andExpect(status().isOk());

                verify(postRepository, times(1)).findTrendingPosts(PageRequest.of(page, size));
        }

        @Test
        void PostsControllerTest_GetPost_ShouldReturnOk() throws Exception {
                // Arrange
                Category category = new Category();
                category.setId(UUID.randomUUID());
                category.setName("Tech");

                UUID postId = UUID.randomUUID();
                Post post = new Post();
                post.setId(postId);
                post.setTitle("Sample Post");
                post.setContent("Sample content");
                post.setCategory(category);
                post.setUser(user);

                when(postRepository.findById(postId)).thenReturn(Optional.of(post));

                // Act & Assert
                mockMvc.perform(get("/api/public/posts/{postId}", postId))
                                .andExpect(status().isOk());

                verify(postRepository, times(1)).findById(postId);
        }

        @Test
        void PostsControllerTest_GetAllPosts_ShouldReturnOk() throws Exception {
                // Arrange
                int page = 0;
                int size = 2;
                String order = "latest";

                Category category = new Category();
                category.setId(UUID.randomUUID());
                category.setName("Tech");

                Post post1 = new Post();
                post1.setId(UUID.randomUUID());
                post1.setTitle("Post 1");
                post1.setCategory(category);
                post1.setUser(user);

                Post post2 = new Post();
                post2.setId(UUID.randomUUID());
                post2.setTitle("Post 2");
                post2.setCategory(category);
                post2.setUser(user);

                List<Post> posts = List.of(post1, post2);
                Page<Post> postPage = new PageImpl<>(posts);

                when(postRepository.findAll(any(Pageable.class))).thenReturn(postPage);

                // Act & Assert
                mockMvc.perform(get("/api/public/posts")
                                .param("page", String.valueOf(page))
                                .param("size", String.valueOf(size))
                                .param("order", order))
                                .andExpect(status().isOk());

                verify(postRepository, times(1)).findAll(any(Pageable.class));
        }

        @Test
        void PostsControllerTest_IncrementViewCount_ShouldReturnNoContent() throws Exception {
                // Arrange
                UUID postId = UUID.randomUUID();

                // Act & Assert
                mockMvc.perform(post("/api/public/posts/{postId}/view", postId))
                                .andExpect(status().isNoContent());

                verify(postService, times(1)).incrementViews(postId);
        }

}
