package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.dto.LoginUserDto;
import com.nelani.blog_land_backend.dto.RegisterUserDto;
import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.repository.UserSocialRepository;
import com.nelani.blog_land_backend.response.LoginResponse;
import com.nelani.blog_land_backend.security.JwtService;
import com.nelani.blog_land_backend.service.impl.AuthServiceImpl;
import com.nelani.blog_land_backend.util.validation.ModerationValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.mockito.Mock;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AuthServiceImplTest {

    @Mock
    private ModerationValidator moderationValidator;

    @Mock
    private UserRepository userRepo;

    @Mock
    private UserSocialRepository userSocialRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;

    @BeforeEach
    public void init() {
        user = User.builder()
                .firstname("firstname")
                .lastname("lastname")
                .email("test-email@test.co.za")
                .provider(Provider.LOCAL)
                .password("Password@123")
                .build();
    }

    @Test
    public void AuthServiceImpl_RegisterUser_ReturnsLoginResponse() {
        // Arrange
        RegisterUserDto dto = RegisterUserDto.builder()
                .email("test-email@test.co.za")
                .firstname("firstname")
                .lastname("lastname")
                .password("Password@123")
                .build();

        when(jwtService.generateToken(any(User.class))).thenReturn("token");

        // Assert
        LoginResponse response = authService.registerUser(dto);
        Assertions.assertThat(response.token()).isEqualTo("token");
        Assertions.assertThat(response.expiresIn()).isEqualTo(86400000);
        Assertions.assertThat(response.user().email()).isEqualTo(dto.email());
    }

    @Test
    public void AuthServiceImpl_LoginUser_ReturnsLoginResponse() {
        // Arrange
        LoginUserDto dto = LoginUserDto.builder()
                .email("test-email@test.co.za")
                .password("Password@123")
                .build();

        when(userRepo.findByEmail(dto.email())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("token");
        when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(true);

        // Assert
        LoginResponse response = authService.loginUser(dto);
        Assertions.assertThat(response.token()).isEqualTo("token");
        Assertions.assertThat(response.expiresIn()).isEqualTo(86400000);
        Assertions.assertThat(response.user().email()).isEqualTo(dto.email());
    }

}
