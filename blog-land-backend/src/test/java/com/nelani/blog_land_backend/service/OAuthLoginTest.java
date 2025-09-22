package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.Util.Validation.ModerationValidator;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OAuthLoginTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ModerationValidator moderationValidator;

    @MockBean
    private AuthService authService;

    @MockBean
    private ModerationClient moderationClient;

    void whenUnauthenticated_thenRedirectToOAuthLogin() throws Exception {
        mockMvc.perform(get("/some-protected-endpoint"))
                .andExpect(status().isUnauthorized()); // 401 without a user
    }

    void whenAuthenticated_thenAccessProtectedEndpoint() throws Exception {
        mockMvc.perform(get("/some-protected-endpoint"))
                .andExpect(status().isOk()); // 200 with a mock user
    }
}
