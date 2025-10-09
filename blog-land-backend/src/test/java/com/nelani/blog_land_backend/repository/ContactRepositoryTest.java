package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.Contact;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
public class ContactRepositoryTest {

    @Autowired
    private ContactRepository contactRepository;

    @Test
    public void ContactRepository_Save_SavesContact() {
        // Arrange
        Contact contact = Contact.builder()
                .fullName("John Doe")
                .email("john@example.com")
                .message("This is a valid message with enough length.")
                .build();

        // Act
        Contact savedContact = contactRepository.save(contact);

        // Assert
        var found = contactRepository.findById(savedContact.getId());
        Assertions.assertThat(found).isPresent();
        Contact foundContact = found.get();
        Assertions.assertThat(savedContact).isNotNull();
        Assertions.assertThat(foundContact.getId()).isEqualTo(savedContact.getId());
        Assertions.assertThat(foundContact.getFullName()).isEqualTo(savedContact.getFullName());
        Assertions.assertThat(foundContact.getEmail()).isEqualTo(savedContact.getEmail());
        Assertions.assertThat(foundContact.getMessage())
                .isEqualTo(savedContact.getMessage());
    }

}
