package com.nelani.blog_land_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nelani.blog_land_backend.dto.LoginUserDto;
import com.nelani.blog_land_backend.dto.RegisterUserDto;
import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.response.LoginResponse;
import com.nelani.blog_land_backend.response.UserResponse;
import com.nelani.blog_land_backend.security.JwtService;
import com.nelani.blog_land_backend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private AuthService authService;

        @MockitoBean
        private JwtService jwtService;

        @MockitoBean
        private UserDetailsService userDetailsService;

        @Autowired
        private ObjectMapper objectMapper;

        private UserResponse user;

        @BeforeEach
        public void init() {
                user = UserResponse.builder()
                                .firstname("firstname")
                                .lastname("lastname")
                                .email("test-email@test.co.za")
                                .provider(Provider.LOCAL)
                                .build();
        }

        @Test
        void AuthControllerTest_Register_ReturnLoginResponse() throws Exception {
                // Arrange
                RegisterUserDto request = RegisterUserDto.builder()
                                .email("test@example.com")
                                .firstname("firstname")
                                .lastname("lastname")
                                .password("password123@")
                                .build();

                LoginResponse mockResponse = new LoginResponse("token123", 8000, user);

                when(authService.registerUser(request)).thenReturn(mockResponse);

                // Asset
                mockMvc.perform(post("/api/public/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.token").value("token123"))
                                .andExpect(jsonPath("$.expiresIn").value(8000))
                                .andExpect(jsonPath("$.user.firstname").value("firstname"))
                                .andExpect(jsonPath("$.user.lastname").value("lastname"))
                                .andExpect(jsonPath("$.user.email").value("test-email@test.co.za"))
                                .andExpect(jsonPath("$.user.provider").value("LOCAL"));
        }

        @Test
        void AuthControllerTest_Login_ReturnLoginResponse() throws Exception {
                // Arrange
                LoginUserDto request = LoginUserDto.builder()
                                .email("test@example.com")
                                .password("password123@")
                                .build();

                LoginResponse mockResponse = new LoginResponse("token123", 8000, user);
                when(authService.loginUser(request)).thenReturn(mockResponse);

                // Act & Assert
                mockMvc.perform(post("/api/public/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").value("token123"))
                                .andExpect(jsonPath("$.expiresIn").value(8000))
                                .andExpect(jsonPath("$.user.firstname").value("firstname"))
                                .andExpect(jsonPath("$.user.lastname").value("lastname"))
                                .andExpect(jsonPath("$.user.email").value("test-email@test.co.za"))
                                .andExpect(jsonPath("$.user.provider").value("LOCAL"));
        }

        @Test
        void AuthControllerTest_Logout_ReturnSuccessMessage() throws Exception {
                // Arrange
                String token = "Bearer token123";

                mockMvc.perform(post("/api/user/auth/log-out")
                                .header("Authorization", token))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Logged out successfully"));
        }

}
