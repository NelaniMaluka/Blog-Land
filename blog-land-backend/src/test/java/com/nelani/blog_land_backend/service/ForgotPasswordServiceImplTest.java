package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.model.PasswordResetToken;
import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.PasswordResetTokenRepository;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.service.impl.ForgotPasswordServiceImpl;
import jakarta.validation.ValidationException;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordServiceImplTest {

        @Mock
        private UserRepository userRepository;

        @Mock
        private PasswordResetTokenRepository passwordResetTokenRepository;

        @Mock
        private EmailService emailService;

        @Mock
        private PasswordEncoder passwordEncoder;

        @InjectMocks
        private ForgotPasswordServiceImpl forgotPasswordService;

        private User testUser;

        @BeforeEach
        void setUp() {
                testUser = User.builder()
                                .id(1L)
                                .email("nelani@example.com")
                                .provider(Provider.LOCAL)
                                .password("encodedPassword")
                                .build();
        }

        @Test
        void requestPasswordReset_ShouldCreateTokenAndSendEmail() {
                Map<String, String> payload = Map.of("email", "nelani@example.com");

                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(testUser));
                when(passwordResetTokenRepository.findByUserAndUsedFalseAndExpiryDateAfter(eq(testUser), any()))
                                .thenReturn(Optional.empty());

                forgotPasswordService.requestPasswordReset(payload);

                verify(passwordResetTokenRepository, times(1)).save(any(PasswordResetToken.class));
                verify(emailService, times(1)).sendPasswordResetEmail(eq("nelani@example.com"), anyString());
        }

        @Test
        void requestPasswordReset_ShouldThrowException_WhenEmailDoesNotExist() {
                Map<String, String> payload = Map.of("email", "wrong-email@gmail.com");

                // Simulate user not found
                when(userRepository.findByEmail("wrong-email@gmail.com"))
                                .thenReturn(Optional.empty());

                // Act + Assert
                ValidationException exception = assertThrows(
                                ValidationException.class,
                                () -> forgotPasswordService.requestPasswordReset(payload));

                assertEquals("No account is associated with that email.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(passwordResetTokenRepository, never()).save(any());
                verify(emailService, never()).sendPasswordResetEmail(any(), any());
        }

        @Test
        void requestPasswordReset_ShouldThrowException_WhenEmailIsEmpty() {
                Map<String, String> payload = Map.of("email", "   ");

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> forgotPasswordService.requestPasswordReset(payload));

                assertEquals("Email is required.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(passwordResetTokenRepository, never()).save(any());
                verify(emailService, never()).sendPasswordResetEmail(any(), any());
        }

        @Test
        void changePassword_ShouldUpdatePasswordAndMarkTokenUsed() {
                String rawToken = "raw-token";
                String hashedToken = DigestUtils.sha256Hex(rawToken);

                PasswordResetToken tokenEntity = PasswordResetToken.builder()
                                .token(hashedToken)
                                .user(testUser)
                                .expiryDate(LocalDateTime.now().plusMinutes(10))
                                .used(false)
                                .createdAt(LocalDateTime.now())
                                .build();

                Map<String, String> payload = Map.of(
                                "token", rawToken,
                                "newPassword", "newStrongPassword@123",
                                "repeatPassword", "newStrongPassword@123");

                when(passwordResetTokenRepository.findByToken(hashedToken))
                                .thenReturn(Optional.of(tokenEntity));
                when(passwordEncoder.encode("newStrongPassword@123"))
                                .thenReturn("encodedNewPassword");

                forgotPasswordService.changePassword(payload);

                assertEquals("encodedNewPassword", testUser.getPassword());
                assertTrue(tokenEntity.isUsed());

                verify(userRepository).save(testUser);
                verify(passwordResetTokenRepository).save(tokenEntity);
        }

        @Test
        void changePassword_ShouldThrowException_WhenTokenIsEmpty() {

                Map<String, String> payload = Map.of(
                                "token", "   ",
                                "newPassword", "newStrongPassword@123",
                                "repeatPassword", "newStrongPassword@123");

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> forgotPasswordService.changePassword(payload));

                assertEquals("Token is required.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(passwordResetTokenRepository, never()).save(any());
        }

        @Test
        void changePassword_ShouldThrowException_WhenNewPasswordIsEmpty() {
                String rawToken = "raw-token";

                Map<String, String> payload = Map.of(
                                "token", rawToken,
                                "newPassword", "   ",
                                "repeatPassword", "newStrongPassword@123");

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> forgotPasswordService.changePassword(payload));

                assertEquals("Password is required.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(passwordResetTokenRepository, never()).save(any());
        }

        @Test
        void changePassword_ShouldThrowException_WhenRepeatPasswordIsEmpty() {
                String rawToken = "raw-token";

                Map<String, String> payload = Map.of(
                                "token", rawToken,
                                "newPassword", "newStrongPassword@123",
                                "repeatPassword", "    ");

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> forgotPasswordService.changePassword(payload));

                assertEquals("Password is required.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(passwordResetTokenRepository, never()).save(any());
        }

        @Test
        void changePassword_ShouldThrowException_WhenTokenDoesNotExist() {
                String rawToken = "raw-token";
                String hashedToken = DigestUtils.sha256Hex(rawToken);

                Map<String, String> payload = Map.of(
                                "token", rawToken,
                                "newPassword", "newStrongPassword@123",
                                "repeatPassword", "newStrongPassword@123");

                // Simulate token not found
                when(passwordResetTokenRepository.findByToken(hashedToken))
                                .thenReturn(Optional.empty());

                // Act + Assert
                NoSuchElementException exception = assertThrows(
                                NoSuchElementException.class,
                                () -> forgotPasswordService.changePassword(payload));

                assertEquals("No value present", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(passwordResetTokenRepository, never()).save(any());
                verify(userRepository, never()).save(any());
        }

}
