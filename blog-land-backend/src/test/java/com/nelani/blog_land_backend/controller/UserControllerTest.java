package com.nelani.blog_land_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nelani.blog_land_backend.dto.UpdateUserDto;
import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.response.LoginResponse;
import com.nelani.blog_land_backend.response.UserResponse;
import com.nelani.blog_land_backend.security.JwtService;
import com.nelani.blog_land_backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private UserService userService;

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
                                .naniId("abc123")
                                .provider(Provider.LOCAL)
                                .build();
        }

        @Test
        void UserControllerTest_UploadProfileImage_ReturnOk() throws Exception {
                // Arrange
                MockMultipartFile file = new MockMultipartFile(
                                "file",
                                "profile.png",
                                MediaType.IMAGE_PNG_VALUE,
                                "dummy image content".getBytes());

                doNothing().when(userService).saveUserProfileImage(file);

                // Act & Assert
                mockMvc.perform(multipart("/api/user/image/upload")
                                .file(file))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Successfully uploaded image"));
        }

        @Test
        @WithMockUser(authorities = "user:write")
        void UserControllerTest_RemoveProfileImage_ReturnNoContent() throws Exception {
                // Arrange
                doNothing().when(userService).removeUserProfileImage();

                // Act & Assert
                mockMvc.perform(delete("/api/user/image/remove"))
                                .andExpect(status().isNoContent());
        }

        @Test
        void UserControllerTest_GetPublicUserDetails_ReturnUserResponse() throws Exception {
                String nanoId = "abc123";

                when(userService.getPublicUserDetails(nanoId)).thenReturn(user);

                // Act & Assert
                mockMvc.perform(get("/api/public/user/{nanoId}", nanoId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.firstname").value("firstname"))
                                .andExpect(jsonPath("$.lastname").value("lastname"))
                                .andExpect(jsonPath("$.email").value("test-email@test.co.za"))
                                .andExpect(jsonPath("$.provider").value("LOCAL"));
        }

        @Test
        @WithMockUser(authorities = "user:read")
        void UserControllerTest_GetUserDetails_ReturnUserResponse() throws Exception {
                // Arrange
                when(userService.getUserDetails()).thenReturn(user);

                // Act & Assert
                mockMvc.perform(get("/api/user/me")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.firstname").value("firstname"))
                                .andExpect(jsonPath("$.lastname").value("lastname"))
                                .andExpect(jsonPath("$.email").value("test-email@test.co.za"))
                                .andExpect(jsonPath("$.naniId").value("abc123"))
                                .andExpect(jsonPath("$.provider").value("LOCAL"));
        }

        @Test
        @WithMockUser(authorities = "user:write")
        void UserControllerTest_UpdateUserDetails_ReturnOk() throws Exception {
                // Arrange
                UpdateUserDto updateDto = UpdateUserDto.builder()
                                .firstname("firstname")
                                .lastname("lastname")
                                .email("test-email@test.co.za")
                                .provider(Provider.LOCAL)
                                .build();

                LoginResponse loginResponse = LoginResponse.builder()
                                .expiresIn(8000)
                                .token("dummy-jwt-token")
                                .user(user)
                                .build();

                when(userService.updateUserDetails(updateDto)).thenReturn(loginResponse);

                // Act & Assert
                mockMvc.perform(put("/api/user/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.expiresIn").value(8000))
                                .andExpect(jsonPath("$.token").value("dummy-jwt-token"))
                                .andExpect(jsonPath("$.user.firstname").value("firstname"))
                                .andExpect(jsonPath("$.user.lastname").value("lastname"))
                                .andExpect(jsonPath("$.user.email").value("test-email@test.co.za"))
                                .andExpect(jsonPath("$.user.naniId").value("abc123"))
                                .andExpect(jsonPath("$.user.provider").value("LOCAL"));
                ;
        }

        @Test
        @WithMockUser(authorities = "user:delete")
        void UserControllerTest_DeleteUserDetails_ReturnNoContent() throws Exception {
                // Arrange
                doNothing().when(userService).deleteUserDetails();

                // Act & Assert
                mockMvc.perform(delete("/api/user/remove"))
                                .andExpect(status().isNoContent());

                verify(userService, times(1)).deleteUserDetails();
        }

}
