package com.nelani.blog_land_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nelani.blog_land_backend.response.LikeResponse;
import com.nelani.blog_land_backend.response.UserResponse;
import com.nelani.blog_land_backend.security.JwtService;
import com.nelani.blog_land_backend.service.LikeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LikeController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class LikeControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private LikeService likeService;

        @MockitoBean
        private JwtService jwtService;

        @MockitoBean
        private UserDetailsService userDetailsService;

        @Test
        void LikeControllerTest_GetPostLikesCount_ReturnOk() throws Exception {
                // Arrange
                UUID postId = UUID.randomUUID();
                long likesCount = 5;

                when(likeService.getPostLikesCount(postId)).thenReturn(likesCount);

                // Act & Assert
                mockMvc.perform(get("/api/public/posts/{postId}/likes", postId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.likesCount").value(likesCount));

                verify(likeService, times(1)).getPostLikesCount(postId);
        }

        @Test
        @WithMockUser(authorities = "like:read")
        void LikeControllerTest_GetUserLikes_ReturnOk() throws Exception {
                // Arrange
                LikeResponse like1 = LikeResponse.builder()
                                .postId(UUID.randomUUID())
                                .likedAt(LocalDateTime.now())
                                .build();

                LikeResponse like2 = LikeResponse.builder()
                                .postId(UUID.randomUUID())
                                .likedAt(LocalDateTime.now())
                                .build();

                List<LikeResponse> likes = List.of(like1, like2);

                when(likeService.getUserLikes()).thenReturn(likes);

                // Act & Assert
                mockMvc.perform(get("/api/user/posts/likes"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].postId").value(like1.postId().toString()));

                verify(likeService, times(1)).getUserLikes();
        }

        @Test
        @WithMockUser(authorities = "like:write")
        void LikeControllerTest_AddLike_ReturnCreated() throws Exception {
                // Arrange
                UUID postId = UUID.randomUUID();

                doNothing().when(likeService).addLike(postId);

                // Act & Assert
                mockMvc.perform(post("/api/user/posts/{postId}/likes", postId))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.message").value("Like added successfully"))
                                .andExpect(jsonPath("$.postId").value(postId.toString()));

                verify(likeService, times(1)).addLike(postId);
        }

        @Test
        @WithMockUser(authorities = "like:delete")
        void LikeControllerTest_RemoveLike_ReturnNoContent() throws Exception {
                // Arrange
                UUID likeId = UUID.randomUUID();
                doNothing().when(likeService).removeLike(likeId);

                // Act & Assert
                mockMvc.perform(delete("/api/user/posts/likes/{likeId}", likeId))
                                .andExpect(status().isNoContent());

                verify(likeService, times(1)).removeLike(likeId);
        }

}
