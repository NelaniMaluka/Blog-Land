package com.nelani.blog_land_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nelani.blog_land_backend.dto.CommentDto;
import com.nelani.blog_land_backend.response.CommentResponse;
import com.nelani.blog_land_backend.security.JwtService;
import com.nelani.blog_land_backend.service.CommentService;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class CommentControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private CommentService commentService;

        @MockitoBean
        private JwtService jwtService;

        @MockitoBean
        private UserDetailsService userDetailsService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void CommentControllerTest_GetCommentsCountByPost_ReturnOk() throws Exception {
                // Arrange
                UUID postId = UUID.randomUUID();
                long count = 7L;

                when(commentService.getCountByPostId(postId)).thenReturn(count);

                // Act & Assert
                mockMvc.perform(get("/api/public/posts/{postId}/comments/count", postId))
                                .andExpect(status().isOk())
                                .andExpect(content().string(String.valueOf(count)));

                verify(commentService, times(1)).getCountByPostId(postId);
        }

        @Test
        void CommentControllerTest_GetAllCommentsByPost_ReturnOk() throws Exception {
                // Arrange
                UUID postId = UUID.randomUUID();

                CommentResponse comment1 = CommentResponse.builder()
                                .id(UUID.randomUUID())
                                .content("First comment")
                                .build();

                CommentResponse comment2 = CommentResponse.builder()
                                .id(UUID.randomUUID())
                                .content("Second comment")
                                .build();

                Page<CommentResponse> commentPage = new PageImpl<>(List.of(comment1, comment2));

                when(commentService.getByPostId(postId, 0, 10)).thenReturn(commentPage);

                // Act & Assert
                mockMvc.perform(get("/api/public/posts/{postId}/comments", postId)
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].content").value("First comment"))
                                .andExpect(jsonPath("$.content[1].content").value("Second comment"));

                verify(commentService, times(1)).getByPostId(postId, 0, 10);
        }

        @Test
        @WithMockUser(authorities = "comment:read")
        void CommentControllerTest_GetAllCommentIdsByPost_ReturnOk() throws Exception {
                // Arrange
                UUID postId = UUID.randomUUID();
                List<UUID> commentIds = List.of(UUID.randomUUID(), UUID.randomUUID());

                when(commentService.getByUserId(postId)).thenReturn(commentIds);

                // Act & Assert
                mockMvc.perform(get("/api/user/posts/{postId}/comments/ids", postId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0]").value(commentIds.get(0).toString()))
                                .andExpect(jsonPath("$[1]").value(commentIds.get(1).toString()));

                verify(commentService, times(1)).getByUserId(postId);
        }

        @Test
        @WithMockUser(authorities = "comment:write")
        void aCommentControllerTest_AddComment_ReturnCreated() throws Exception {
                // Arrange
                UUID postId = UUID.randomUUID();
                CommentDto commentDto = CommentDto.builder()
                                .content("This is a test comment")
                                .build();

                String json = objectMapper.writeValueAsString(commentDto);

                // Act & Assert
                mockMvc.perform(post("/api/user/posts/{postId}/comments", postId)
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isCreated())
                                .andExpect(content().string("Comment added successfully"));

                verify(commentService, times(1)).addComment(eq(postId), any(CommentDto.class));
        }

        @Test
        @WithMockUser(authorities = "comment:write")
        void CommentControllerTest_UpdateComment_ReturnOk() throws Exception {
                // Arrange
                UUID postId = UUID.randomUUID();
                UUID commentId = UUID.randomUUID();
                CommentDto commentDto = CommentDto.builder()
                                .content("Updated comment content")
                                .build();

                String json = objectMapper.writeValueAsString(commentDto);

                // Act & Assert
                mockMvc.perform(put("/api/user/posts/{postId}/comments/{commentId}", postId, commentId)
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Comment updated successfully"));

                verify(commentService, times(1)).updateComment(eq(postId), eq(commentId), any(CommentDto.class));
        }

        @Test
        @WithMockUser(authorities = "comment:delete")
        void CommentControllerTest_DeleteComment_ReturnNoContent() throws Exception {
                // Arrange
                UUID commentId = UUID.randomUUID();

                // Act & Assert
                mockMvc.perform(delete("/api/user/posts/comments/{commentId}", commentId))
                                .andExpect(status().isNoContent());

                verify(commentService, times(1)).deleteComment(commentId);
        }

}
