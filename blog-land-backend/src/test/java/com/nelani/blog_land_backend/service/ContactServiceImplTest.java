package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.model.Contact;
import com.nelani.blog_land_backend.repository.ContactRepository;
import com.nelani.blog_land_backend.service.impl.ContactServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ContactServiceImplTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactServiceImpl contactService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getInfo_ShouldSaveValidContact() {
        // Arrange
        Contact contact = new Contact();
        contact.setFullName("Nelani Maluka");
        contact.setEmail("nelani@example.com");
        contact.setMessage("This is a test message.");

        // Act
        contactService.getInfo(contact);

        // Assert
        ArgumentCaptor<Contact> contactCaptor = ArgumentCaptor.forClass(Contact.class);
        verify(contactRepository, times(1)).save(contactCaptor.capture());
        Contact savedContact = contactCaptor.getValue();

        assertEquals("Nelani Maluka", savedContact.getFullName());
        assertEquals("nelani@example.com", savedContact.getEmail());
        assertEquals("This is a test message.", savedContact.getMessage());
    }

    @Test
    void getInfo_ShouldThrowException_WhenFullNameIsEmpty() {
        // Arrange
        Contact contact = new Contact();
        contact.setFullName("   "); // invalid
        contact.setEmail("nelani@example.com");
        contact.setMessage("Message");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> contactService.getInfo(contact)
        );

        assertTrue(exception.getMessage().contains("Full Name is required."));

        // Ensure save is never called because it should fail
        verify(contactRepository, never()).save(any());
    }

    @Test
    void getInfo_ShouldThrowException_WhenEmailIsInvalid() {
        // Arrange
        Contact contact = new Contact();
        contact.setFullName("Nelani Maluka");
        contact.setEmail("invalid-email"); // not a valid email
        contact.setMessage("Message");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> contactService.getInfo(contact)
        );

        assertTrue(exception.getMessage().toLowerCase().contains("email"));

        // Ensure save is never called because it should fail
        verify(contactRepository, never()).save(any());
    }
}

