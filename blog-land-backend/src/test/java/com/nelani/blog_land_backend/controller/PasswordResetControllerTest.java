package com.nelani.blog_land_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nelani.blog_land_backend.dto.ForgotPasswordDto;
import com.nelani.blog_land_backend.dto.PasswordDto;
import com.nelani.blog_land_backend.security.JwtService;
import com.nelani.blog_land_backend.service.PasswordResetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PasswordResetController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class PasswordResetControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private PasswordResetService passwordResetService;

        @MockitoBean
        private JwtService jwtService;

        @MockitoBean
        private UserDetailsService userDetailsService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void PasswordResetControllerTest_RequestPasswordReset_ReturnCreated() throws Exception {
                // Arrange
                String email = "test@example.com";

                // Act & Assert
                mockMvc.perform(post("/api/public/password/reset")
                                .param("email", email))
                                .andExpect(status().isCreated())
                                .andExpect(content().string("Success, password reset link sent to your email."));

                verify(passwordResetService, times(1)).requestPasswordReset(email);
        }

        @Test
        void PasswordResetControllerTest_ResetPassword_ReturnOk() throws Exception {
                // Arrange
                String token = "sample-token";
                ForgotPasswordDto passwordDto = ForgotPasswordDto.builder()
                                .newPassword("NewPassword123!")
                                .repeatPassword("NewPassword123!")
                                .build();
                String json = objectMapper.writeValueAsString(passwordDto);

                // Act & Assert
                mockMvc.perform(put("/api/public/password/reset/{token}", token)
                                .contentType("application/json")
                                .content(json))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Success, your password was changed successfully!"));

                verify(passwordResetService, times(1)).changePassword(any(ForgotPasswordDto.class), eq(token));
        }

        @Test
        @WithMockUser(authorities = "user:write")
        void PasswordResetControllerTest_ChangePassword_ReturnOk() throws Exception {
                // Arrange
                PasswordDto passwordDto = PasswordDto.builder()
                                .oldPassword("OldPassword123!")
                                .newPassword("NewPassword123!")
                                .repeatPassword("NewPassword123!")
                                .build();
                String json = objectMapper.writeValueAsString(passwordDto);

                // Act & Assert
                mockMvc.perform(put("/api/user/password")
                                .contentType("application/json")
                                .content(json))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Success, your password was changed successfully!"));

                verify(passwordResetService, times(1)).changePasswordWithOldPassword(any(PasswordDto.class));
        }

}
