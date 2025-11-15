package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.dto.ContactDto;
import com.nelani.blog_land_backend.model.Contact;
import com.nelani.blog_land_backend.repository.ContactRepository;
import com.nelani.blog_land_backend.service.impl.ContactServiceImpl;
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
public class ContactServiceImplTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactServiceImpl contactService;

    @Test
    public void ContactServiceImplTest_SaveInfo_returnVoid() {
        // Arrange
        ContactDto dto = ContactDto.builder()
                .email("test-email@test.co.za")
                .message("Long message for the contact.")
                .fullName("fullName")
                .build();

        // Act
        contactService.saveInfo(dto);

        // Assert
        verify(contactRepository, times(1)).save(any(Contact.class));
    }
}
