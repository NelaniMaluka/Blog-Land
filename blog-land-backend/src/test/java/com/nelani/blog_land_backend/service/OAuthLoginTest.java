package com.nelani.blog_land_backend.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.nelani.blog_land_backend.Util.Validation.ModerationValidator;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OAuthLoginTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ModerationValidator moderationValidator;

    @MockitoBean
    private ModerationClient moderationClient;

    @Test
    void whenUnauthenticated_thenRedirectToOAuthLogin() throws Exception {
        mockMvc.perform(get("/some-protected-endpoint"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void whenAuthenticated_thenAccessProtectedEndpoint() throws Exception {
        mockMvc.perform(get("/some-protected-endpoint"))
                .andExpect(status().isOk());
    }
}
