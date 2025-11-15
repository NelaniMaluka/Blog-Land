package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.cache.UserCacheHelper;
import com.nelani.blog_land_backend.dto.UpdateUserDto;
import com.nelani.blog_land_backend.model.ExperienceLevel;
import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.model.UserSocial;
import com.nelani.blog_land_backend.repository.*;
import com.nelani.blog_land_backend.security.JwtService;
import com.nelani.blog_land_backend.service.impl.UserServiceImpl;
import com.nelani.blog_land_backend.sockets.UserSocket;
import com.nelani.blog_land_backend.util.validation.ModerationValidator;
import com.nelani.blog_land_backend.util.validation.UserValidation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserServiceImplTest {

    @Mock
    private ModerationValidator moderationValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSocialRepository userSocialRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PasswordResetRepository passwordResetRepository;

    @Mock
    private UserSocket userSocket;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserCacheHelper userCacheHelper;

    @Mock
    private UserValidation userValidation;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserSocial userSocial;

    @BeforeEach
    public void init() {
        user = User.builder()
                .firstname("firstname")
                .lastname("lastname")
                .email("test-email@test.co.za")
                .provider(Provider.LOCAL)
                .password("Password@123")
                .build();

        userSocial = UserSocial.builder()
                .platform("platform")
                .url("url")
                .user(user)
                .build();
    }

    @Test
    public void UserServiceImplTest_getUserDetails_ReturnsUserResponse() {
        // Arrange
        when(userValidation.getAuthenticatedUser()).thenReturn(user);
        when(userSocialRepository.findByUser(any(User.class))).thenReturn(List.of(userSocial));

        // Assert
        var result = userService.getUserDetails();
        Assertions.assertThat(result.email()).isEqualTo(user.getEmail());
        Assertions.assertThat(result.firstname()).isEqualTo(user.getFirstname());
        Assertions.assertThat(result.lastname()).isEqualTo(user.getLastname());
        Assertions.assertThat(result.provider()).isEqualTo(Provider.LOCAL);
        Assertions.assertThat(result.socials().get(userSocial.getPlatform()))
                .isEqualTo(userSocial.getUrl());
    }

    @Test
    public void UserServiceImplTest_UpdateUserDetails_ReturnsLoginResponse() {
        // Arrange
        UpdateUserDto dto = UpdateUserDto.builder()
                .email(user.getEmail())
                .provider(Provider.LOCAL)
                .experience(ExperienceLevel.CASUAL_POSTER)
                .firstname("firstname change")
                .lastname("lastname Change")
                .title("title")
                .socials(Map.of("email", "john@example.com"))
                .build();

        when(userValidation.getAuthenticatedUser()).thenReturn(user);
        when(userSocialRepository.findByUser(any(User.class))).thenReturn(List.of(userSocial));
        when(jwtService.generateToken(any(User.class))).thenReturn("token");

        // Assert
        var response = userService.updateUserDetails(dto);
        Assertions.assertThat(response.token()).isEqualTo("token");
        Assertions.assertThat(response.expiresIn()).isEqualTo(86400000);
        Assertions.assertThat(response.user().email()).isEqualTo(user.getEmail());
        Assertions.assertThat(response.user().experience()).isEqualTo(dto.experience());
        Assertions.assertThat(response.user().firstname()).isEqualTo(dto.firstname());
        Assertions.assertThat(response.user().lastname()).isEqualTo(dto.lastname());
        Assertions.assertThat(response.user().title()).isEqualTo(dto.title());
    }

    @Test
    public void UserServiceImplTest_DeleteUserDetails_ReturnsVoid() {
        // Arrange
        when(userValidation.getAuthenticatedUser()).thenReturn(user);

        // Assert
        userService.deleteUserDetails();
        verify(likeRepository, times(1)).deleteByUser(user);
        verify(commentRepository, times(1)).deleteByUser(user);
        verify(postRepository, times(1)).deleteByUser(user);
        verify(likeRepository, times(1)).deleteByUser(user);
        verify(passwordResetRepository, times(1)).deleteByUser(user);
        verify(userSocialRepository, times(1)).deleteByUser(user);
        verify(userRepository, times(1)).delete(user);
    }

}
