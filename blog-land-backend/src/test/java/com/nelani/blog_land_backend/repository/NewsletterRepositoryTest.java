package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.Newsletter;
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
public class NewsletterRepositoryTest {

    @Autowired
    private NewsletterRepository newsletterRepository;

    @Test
    public void NewsletterRepository_FindByEmail_ReturnsNewsletter() {
        // Arrange
        Newsletter newsletter = Newsletter.builder()
                .email("john@example.com")
                .build();

        // Act
        newsletterRepository.save(newsletter);

        // Assert
        var found = newsletterRepository.findByEmail(newsletter.getEmail());
        Assertions.assertThat(found).isPresent();
        Newsletter foundNewsletter = found.get();
        Assertions.assertThat(foundNewsletter).isNotNull();
        Assertions.assertThat(foundNewsletter.getId()).isEqualTo(newsletter.getId());
        Assertions.assertThat(foundNewsletter.getEmail()).isEqualTo(newsletter.getEmail());
    }

}
