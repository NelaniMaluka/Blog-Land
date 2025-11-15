package com.nelani.blog_land_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nelani.blog_land_backend.dto.PostDto;
import com.nelani.blog_land_backend.response.PostResponse;
import com.nelani.blog_land_backend.security.JwtService;
import com.nelani.blog_land_backend.service.UserPostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserPostsController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class UserPostsControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private UserPostService userPostService;

        @MockitoBean
        private JwtService jwtService;

        @MockitoBean
        private UserDetailsService userDetailsService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @WithMockUser(authorities = "post:read")
        void UserPostsControllerTest_GetAllPostsByUserId_ReturnOk() throws Exception {
                // Arrange
                PostResponse post1 = PostResponse.builder()
                                .id(UUID.randomUUID())
                                .title("Post One")
                                .content("Content of post one")
                                .build();

                PostResponse post2 = PostResponse.builder()
                                .id(UUID.randomUUID())
                                .title("Post Two")
                                .content("Content of post two")
                                .build();

                Page<PostResponse> postPage = new PageImpl<>(List.of(post1, post2));

                when(userPostService.getByUserId(0, 10)).thenReturn(postPage);

                // Act & Assert
                mockMvc.perform(get("/api/user/posts")
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].title").value("Post One"))
                                .andExpect(jsonPath("$.content[1].title").value("Post Two"));

                verify(userPostService, times(1)).getByUserId(0, 10);
        }

        @Test
        @WithMockUser(authorities = "post:write")
        void UserPostsControllerTest_AddPostsByUserId_ReturnCreated() throws Exception {
                // Arrange
                PostDto postDto = PostDto.builder()
                                .title("New Post")
                                .content("Post content")
                                .categoryId(UUID.randomUUID())
                                .build();

                String json = objectMapper.writeValueAsString(postDto);

                // Act & Assert
                mockMvc.perform(post("/api/user/posts/add")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isCreated())
                                .andExpect(content().string("Success, Your post was successfully added"));

                verify(userPostService, times(1)).addPost(any(PostDto.class));
        }

        @Test
        @WithMockUser(authorities = "post:write")
        void UserPostsControllerTest_UpdateUserPost_ReturnOk() throws Exception {
                // Arrange
                PostDto postDto = PostDto.builder()
                                .id(UUID.randomUUID())
                                .title("Updated Post")
                                .content("Updated content")
                                .categoryId(UUID.randomUUID())
                                .build();

                String json = objectMapper.writeValueAsString(postDto);

                // Act & Assert
                mockMvc.perform(put("/api/user/posts/update")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Success, Your post was successfully updated"));

                verify(userPostService, times(1)).updatePost(any(PostDto.class));
        }

        @Test
        @WithMockUser(authorities = "post:delete")
        void UserPostsControllerTest_DeleteUserPost_ReturnNoContent() throws Exception {
                UUID postId = UUID.randomUUID();

                mockMvc.perform(delete("/api/user/posts/remove")
                                .param("postId", postId.toString()))
                                .andExpect(status().isNoContent());

                verify(userPostService, times(1)).deletePost(postId);
        }

}
