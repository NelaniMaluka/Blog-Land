package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.model.Newsletter;
import com.nelani.blog_land_backend.repository.NewsletterRepository;
import com.nelani.blog_land_backend.service.impl.NewsletterServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class NewsletterServiceImplTest {

    @Mock
    private NewsletterRepository newsletterRepository;

    @InjectMocks
    private NewsletterServiceImpl newsletterService;

    @Test
    public void NewsletterServiceImplTest_AddEmail_ReturnVoid() {
        // Arrange
        String email = "test-email@test.co.za";

        // Act
        newsletterService.addEmail(email);

        // Assert
        verify(newsletterRepository, times(1)).save(any(Newsletter.class));
    }
}
