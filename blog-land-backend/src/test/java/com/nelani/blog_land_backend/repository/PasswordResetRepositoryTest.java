package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.PasswordReset;
import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
public class PasswordResetRepositoryTest {

    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Autowired
    private UserRepository userRepository;

    private PasswordReset passwordReset;
    private User user;

    @BeforeEach
    public void init() {
        user = User.builder()
                .firstname("firstname")
                .lastname("lastname")
                .email("john@example.com")
                .provider(Provider.LOCAL)
                .build();

        userRepository.save(user);

        LocalDateTime tokenExpireTime = LocalDateTime.now().plusMinutes(15);

        passwordReset = PasswordReset.builder()
                .token("token")
                .user(user)
                .expiryDate(tokenExpireTime)
                .build();
    }

    @Test
    public void PasswordResetRepository_FindByToken_ReturnsToken() {
        // Act
        passwordResetRepository.save(passwordReset);

        // Assert
        var passwordReset1 = passwordResetRepository.findByToken(passwordReset.getToken());
        Assertions.assertThat(passwordReset1).isPresent();
        PasswordReset foundToken = passwordReset1.get();
        Assertions.assertThat(foundToken.getId()).isEqualTo(passwordReset.getId());
        Assertions.assertThat(foundToken.getToken()).isEqualTo(passwordReset.getToken());
        Assertions.assertThat(foundToken.getUser()).isEqualTo(user);
    }

    @Test
    public void PasswordResetRepository_FindByUserAndUsedFalseAndExpiryDateAfter_ReturnsToken() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        passwordResetRepository.save(passwordReset);

        // Assert
        var passwordReset1 = passwordResetRepository.findByUserAndUsedFalseAndExpiryDateAfter(user, now);
        Assertions.assertThat(passwordReset1).isPresent();
        PasswordReset foundToken = passwordReset1.get();
        Assertions.assertThat(foundToken.getId()).isEqualTo(passwordReset.getId());
        Assertions.assertThat(foundToken.getToken()).isEqualTo(passwordReset.getToken());
        Assertions.assertThat(foundToken.getUser()).isEqualTo(user);
    }

    @Test
    public void PasswordResetRepository_FindByUserAndUsedFalseAndExpiryDateAfter_ReturnsEmpty() {
        // Arrange
        LocalDateTime now = LocalDateTime.now().plusMinutes(20);

        // Act
        passwordResetRepository.save(passwordReset);

        // Assert
        var passwordReset1 = passwordResetRepository.findByUserAndUsedFalseAndExpiryDateAfter(user, now);
        Assertions.assertThat(passwordReset1).isEmpty();
        Assertions.assertThat(passwordReset1).isNotNull();
    }

    @Test
    public void PasswordResetRepository_DeleteByUser_DeletesPasswordReset() {
        // Act
        passwordResetRepository.save(passwordReset);

        // Assert
        var count = passwordResetRepository.deleteByUser(user);
        Assertions.assertThat(count).isEqualTo(1);
    }

}
