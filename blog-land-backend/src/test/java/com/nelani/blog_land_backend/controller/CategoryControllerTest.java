package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.model.Category;
import com.nelani.blog_land_backend.repository.CategoryRepository;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.response.PostResponse;
import com.nelani.blog_land_backend.security.JwtService;
import com.nelani.blog_land_backend.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class CategoryControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private CategoryRepository categoryRepository;

        @MockitoBean
        private PostRepository postRepository;

        @MockitoBean
        private CategoryService categoryService;

        @MockitoBean
        private JwtService jwtService;

        @MockitoBean
        private UserDetailsService userDetailsService;

        @Test
        void CategoryControllerTest_GetCategories_ReturnListOfCategories() throws Exception {
                UUID id1 = UUID.randomUUID();
                UUID id2 = UUID.randomUUID();

                // Arrange
                Category category1 = new Category();
                category1.setId(id1);
                category1.setName("Tech");

                Category category2 = new Category();
                category2.setId(id2);
                category2.setName("Health");

                List<Category> categories = Arrays.asList(category1, category2);

                when(categoryRepository.findAll()).thenReturn(categories);
                when(postRepository.countByCategoryId(id1)).thenReturn(5);
                when(postRepository.countByCategoryId(id2)).thenReturn(3);

                // Act & Assert
                mockMvc.perform(get("/api/public/posts/categories")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(id1.toString()))
                                .andExpect(jsonPath("$[0].name").value("Tech"))
                                .andExpect(jsonPath("$[0].postCount").value(5))
                                .andExpect(jsonPath("$[1].id").value(id2.toString()))
                                .andExpect(jsonPath("$[1].name").value("Health"))
                                .andExpect(jsonPath("$[1].postCount").value(3));
        }

        @Test
        void CategoryControllerTest_GetAllPostsByCategory_ReturnPagedPosts() throws Exception {
                UUID categoryId = UUID.randomUUID();

                // Arrange
                PostResponse post1 = PostResponse.builder()
                                .id(UUID.randomUUID())
                                .title("Post 1")
                                .content("Content 1")
                                .userId("userId1")
                                .build();

                PostResponse post2 = PostResponse.builder()
                                .id(UUID.randomUUID())
                                .title("Post 2")
                                .content("Content 2")
                                .userId("userId2")
                                .build();

                Page<PostResponse> page = new PageImpl<>(Arrays.asList(post1, post2));

                when(categoryService.getByCategoryId(categoryId, 0, 10, "asc")).thenReturn(page);

                // Act & Assert
                mockMvc.perform(get("/api/public/posts/categories/{categoryId}", categoryId)
                                .param("page", "0")
                                .param("size", "10")
                                .param("order", "asc")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].title").value("Post 1"))
                                .andExpect(jsonPath("$.content[1].title").value("Post 2"))
                                .andExpect(jsonPath("$.content.length()").value(2));
        }
}
