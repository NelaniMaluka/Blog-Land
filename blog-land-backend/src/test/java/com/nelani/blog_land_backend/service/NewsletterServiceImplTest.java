package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.model.Newsletter;
import com.nelani.blog_land_backend.repository.NewsletterRepository;
import com.nelani.blog_land_backend.service.impl.NewsletterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class NewsletterServiceImplTest {

    @Mock
    private NewsletterRepository newsletterRepository;

    @InjectMocks
    private NewsletterServiceImpl newsletterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addEmail_ShouldSaveValidNewsltter() {
        // Arrange
        Newsletter newsletter = new Newsletter();
        newsletter.setEmail("nelani@example.com");

        // Act
        newsletterService.addEmail(newsletter);

        // Assert
        ArgumentCaptor<Newsletter> newsletterCaptor = ArgumentCaptor.forClass(Newsletter.class);
        verify(newsletterRepository, times(1)).save(newsletterCaptor.capture());
        Newsletter savedNewsletter = newsletterCaptor.getValue();

        assertEquals("nelani@example.com", savedNewsletter.getEmail());
    }

    @Test
    void addEmail_ShouldThrowException_WhenEmailIsInvalid() {
        // Arrange
        Newsletter newsletter = new Newsletter();
        newsletter.setEmail("invalid-email");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> newsletterService.addEmail(newsletter));

        assertTrue(exception.getMessage().toLowerCase().contains("email"));

        // Ensure save is never called because it should fail
        verify(newsletterRepository, never()).save(any());
    }

}
