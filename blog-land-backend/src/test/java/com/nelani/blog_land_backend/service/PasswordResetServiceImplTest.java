package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.dto.ForgotPasswordDto;
import com.nelani.blog_land_backend.dto.PasswordDto;
import com.nelani.blog_land_backend.model.PasswordReset;
import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.notifications.EmailService;
import com.nelani.blog_land_backend.repository.PasswordResetRepository;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.service.impl.PasswordResetServiceImpl;
import com.nelani.blog_land_backend.util.validation.UserValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class PasswordResetServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidation userValidation;

    @Mock
    private PasswordResetRepository passwordResetRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PasswordResetServiceImpl passwordResetService;

    private User user;

    @BeforeEach
    public void init() {
        user = User.builder()
                .firstname("firstname")
                .lastname("lastname")
                .email("test-email@test.co.za")
                .provider(Provider.LOCAL)
                .password(passwordEncoder.encode("Password@123"))
                .build();
    }

    @Test
    public void PasswordResetServiceImplTest_RequestPasswordReset_ReturnVoid() {
        // Arrange
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Act
        passwordResetService.requestPasswordReset(user.getEmail());

        // Assert
        verify(passwordResetRepository, times(1)).save(any(PasswordReset.class));
        verify(emailService, times(1)).sendPasswordResetEmail(any(String.class), any(String.class));
    }

    @Test
    public void PasswordResetServiceImplTest_ChangePassword_ReturnVoid() {
        // Arrange
        PasswordReset ps = PasswordReset.builder()
                .token("token")
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();
        ForgotPasswordDto dto = ForgotPasswordDto.builder()
                .newPassword("newPassword123@")
                .repeatPassword("newPassword123@")
                .build();

        when(passwordResetRepository.findByToken(any(String.class))).thenReturn(Optional.of(ps));

        // Act
        passwordResetService.changePassword(dto, "token");

        // Assert
        verify(passwordResetRepository, times(1)).save(any(PasswordReset.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void PasswordResetServiceImplTest_ChangePasswordWithOldPassword_ReturnVoid() {
        // Arrange
        PasswordDto dto = PasswordDto.builder()
                .oldPassword("Password@123")
                .newPassword("newPassword123@")
                .repeatPassword("newPassword123@")
                .build();

        when(userValidation.getAuthenticatedUser()).thenReturn(user);
        when(passwordEncoder.matches(eq("Password@123"), any())).thenReturn(true);
        when(passwordEncoder.matches(eq("newPassword123@"), any())).thenReturn(false);

        // Act
        passwordResetService.changePasswordWithOldPassword(dto);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
    }

}
