package com.nelani.blog_land_backend.repository;

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

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void init() {
        user = User.builder()
                .firstname("firstname")
                .lastname("lastname")
                .email("john@example.com")
                .provider(Provider.LOCAL)
                .build();
    }

    @Test
    public void UserRepository_FindById_ReturnsUser() {
        // Act
        User savedUser = userRepository.save(user);

        // Assert
        var optionalUser = userRepository.findById(savedUser.getId());
        Assertions.assertThat(optionalUser).isPresent();
        User foundUser = optionalUser.get();
        Assertions.assertThat(foundUser).isNotNull();
        Assertions.assertThat(foundUser.getFirstname()).isEqualTo(savedUser.getFirstname());
        Assertions.assertThat(foundUser.getLastname()).isEqualTo(savedUser.getLastname());
        Assertions.assertThat(foundUser.getEmail()).isEqualTo(savedUser.getEmail());
    }

    @Test
    public void UserRepository_FindByNaniId_ReturnsUser() {
        // Act
        User savedUser = userRepository.save(user);

        // Assert
        var optionalUser = userRepository.findByNaniId(savedUser.getNaniId());
        Assertions.assertThat(optionalUser).isPresent();
        User foundUser = optionalUser.get();
        Assertions.assertThat(foundUser).isNotNull();
        Assertions.assertThat(foundUser.getFirstname()).isEqualTo(savedUser.getFirstname());
        Assertions.assertThat(foundUser.getLastname()).isEqualTo(savedUser.getLastname());
        Assertions.assertThat(foundUser.getEmail()).isEqualTo(savedUser.getEmail());
    }

    @Test
    public void UserRepository_FindByEmail_ReturnsUser() {
        // Act
        User savedUser = userRepository.save(user);

        // Assert
        var optionalUser = userRepository.findByEmail(savedUser.getEmail());
        Assertions.assertThat(optionalUser).isPresent();
        User foundUser = optionalUser.get();
        Assertions.assertThat(foundUser).isNotNull();
        Assertions.assertThat(foundUser.getFirstname()).isEqualTo(savedUser.getFirstname());
        Assertions.assertThat(foundUser.getLastname()).isEqualTo(savedUser.getLastname());
        Assertions.assertThat(foundUser.getEmail()).isEqualTo(savedUser.getEmail());
    }

    @Test
    public void UserRepository_Delete_DeletesUser() {
        // Act
        User savedUser = userRepository.save(user);
        userRepository.delete(savedUser);

        // Assert
        var optionalUser = userRepository.findById(savedUser.getId());
        Assertions.assertThat(optionalUser).isEmpty();
        Assertions.assertThat(optionalUser).isNotNull();
    }

}
