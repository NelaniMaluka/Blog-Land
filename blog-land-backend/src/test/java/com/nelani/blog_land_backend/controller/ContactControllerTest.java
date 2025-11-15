package com.nelani.blog_land_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nelani.blog_land_backend.dto.ContactDto;
import com.nelani.blog_land_backend.security.JwtService;
import com.nelani.blog_land_backend.service.ContactService;
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

@WebMvcTest(controllers = ContactController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContactService contactService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void ContactControllerTest_GetContactMessage_ReturnCreated() throws Exception {
        // Arrange
        ContactDto contactDto = ContactDto.builder()
                .fullName("John Doe")
                .email("john@example.com")
                .message("Hello, this is a test message.")
                .build();

        doNothing().when(contactService).saveInfo(contactDto);

        // Act & Assert
        mockMvc.perform(post("/api/public/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contactDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Success, we received your message. Thank you for reaching out."));
    }

}
