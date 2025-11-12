package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.security.JwtService;
import com.nelani.blog_land_backend.service.NewsletterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NewsletterController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class NewsletterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NewsletterService newsletterService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void NewsletterControllerTest_AddNewsletter_ReturnCreated() throws Exception {
        // Arrange
        String email = "test@example.com";
        doNothing().when(newsletterService).addEmail(email);

        // Act & Assert
        mockMvc.perform(post("/api/public/newsletter")
                .param("email", email)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .string("Success, we received your email. Thank you for subscribing to our newsletter."));
    }
}
