package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.model.UserSocial;
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
public class UserSocialRepositoryTest {

    @Autowired
    private UserSocialRepository userSocialRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private UserSocial userSocial;

    @BeforeEach
    public void init() {
        user = User.builder()
                .firstname("firstname")
                .lastname("lastname")
                .email("john@example.com")
                .provider(Provider.LOCAL)
                .build();

        userRepository.save(user);

        userSocial = UserSocial.builder()
                .user(user)
                .url("url")
                .platform("platform").build();
    }

    @Test
    public void UserSocialRepository_FindByUser_ReturnsUserSocialList() {
        // Arrange
        userSocialRepository.save(userSocial);

        // Assert
        var found = userSocialRepository.findByUser(user);
        Assertions.assertThat(found.size()).isEqualTo(1);
        Assertions.assertThat(found.get(0).getUser().getId()).isEqualTo(user.getId());
        Assertions.assertThat(found.get(0).getPlatform()).isEqualTo(userSocial.getPlatform());
        Assertions.assertThat(found.get(0).getUrl()).isEqualTo(userSocial.getUrl());
    }

    @Test
    public void UserSocialRepository_DeleteByUser_DeletesUserSocial() {
        // Arrange
        userSocialRepository.save(userSocial);

        // Assert
        var count = userSocialRepository.deleteByUser(user);
        Assertions.assertThat(count).isEqualTo(1);
    }

}
