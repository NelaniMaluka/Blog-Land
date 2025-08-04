package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.Util.JwtUtil;
import com.nelani.blog_land_backend.Util.Validation.ModerationValidator;
import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.service.impl.AuthServiceImpl;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceImplTest {

        @Mock
        private UserRepository userRepository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @Mock
        private ModerationValidator moderationValidator;

        @Mock
        private JwtUtil jwtUtils;

        @InjectMocks
        private AuthServiceImpl authService;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
        }

        @Test
        void registerUser_ShouldSaveValidUser() {
                // Arrange
                User user = new User();
                user.setFirstname("Nelani");
                user.setLastname("Maluka");
                user.setEmail("nelani@example.com");
                user.setPassword("strongPassword@123");

                when(passwordEncoder.encode("strongPassword@123"))
                                .thenReturn("encodedPassword123");
                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).userModeration(any(User.class));

                // Act
                authService.registerUser(user);

                // Assert
                ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
                verify(userRepository, times(1)).save(userCaptor.capture());
                verify(moderationValidator, times(1)).userModeration(user);

                User savedUser = userCaptor.getValue();
                assertEquals("Nelani", savedUser.getFirstname());
                assertEquals("Maluka", savedUser.getLastname());
                assertEquals("nelani@example.com", savedUser.getEmail());
                assertEquals("encodedPassword123", savedUser.getPassword());
        }

        @Test
        void registerUser_ShouldThrowException_WhenFirstnameIsEmpty() {
                // Arrange
                User user = new User();
                user.setFirstname("   "); // invalid
                user.setLastname("Maluka");
                user.setEmail("nelani@example.com");
                user.setPassword("strongPassword@123");

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> authService.registerUser(user));

                assertEquals("Firstname is required.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

        @Test
        void registerUser_ShouldThrowException_WhenLastnameIsEmpty() {
                // Arrange
                User user = new User();
                user.setFirstname("Nelani");
                user.setLastname("    "); // invalid
                user.setEmail("nelani@example.com");
                user.setPassword("strongPassword@123");

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> authService.registerUser(user));

                assertEquals("Lastname is required.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

        @Test
        void registerUser_ShouldThrowException_WhenEmailIsInvalid() {
                // Arrange
                User user = new User();
                user.setFirstname("Nelani");
                user.setLastname("Maluka");
                user.setEmail("invalid-email"); // invalid
                user.setPassword("strongPassword@123");

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> authService.registerUser(user));

                assertEquals("The provided email address is not valid. Please provide a valid email address.",
                                exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

        @Test
        void registerUser_ShouldThrowException_WhenPasswordIsInvalid() {
                // Arrange
                User user = new User();
                user.setFirstname("Nelani");
                user.setLastname("Maluka");
                user.setEmail("nelani@example.com");
                user.setPassword("wrong-password"); // invalid

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> authService.registerUser(user));

                assertEquals("Password must contain at least 8 characters, an uppercase letter, a digit, and a special character.",
                                exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

        @Test
        void registerUser_ShouldThrowException_WhenUserFailModeration() {
                // Arrange
                User user = new User();
                user.setFirstname("fuck");
                user.setLastname("Maluka");
                user.setEmail("nelani@example.com");
                user.setPassword("strongPassword@123");

                when(passwordEncoder.encode("strongPassword@123"))
                                .thenReturn("encodedPassword123");
                doThrow(new ValidationException("User did not pass moderation."))
                                .when(moderationValidator).userModeration(any(User.class));

                // Act + Assert
                ValidationException exception = assertThrows(
                                ValidationException.class,
                                () -> authService.registerUser(user));

                assertEquals("User did not pass moderation.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

        @Test
        void registerUser_ShouldThrowException_WhenUserAlreadyExists() {
                // Arrange
                User user = new User();
                user.setFirstname("Nelani");
                user.setLastname("Maluka");
                user.setEmail("nelani@example.com");
                user.setPassword("strongPassword@123");

                // Mock repository to simulate that user already exists
                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(new User())); // Simulate found user

                when(passwordEncoder.encode("strongPassword@123"))
                                .thenReturn("encodedPassword123");

                // Act + Assert
                ValidationException exception = assertThrows(
                                ValidationException.class,
                                () -> authService.registerUser(user));

                assertEquals("User already exists with the provided email.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

        @Test
        void loginUser_ShouldLogInValidUser() {
                // Arrange
                Map<String, String> payload = new HashMap<>();
                payload.put("email", "nelani@example.com");
                payload.put("password", "strongPassword@123");

                // Fake user as if it's already in the database
                User existingUser = new User();
                existingUser.setEmail("nelani@example.com");
                existingUser.setPassword("encodedPassword123");
                existingUser.setProvider(Provider.LOCAL);

                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(existingUser));
                when(passwordEncoder.matches("strongPassword@123", "encodedPassword123"))
                                .thenReturn(true);
                when(jwtUtils.generateJwtToken(any(User.class)))
                                .thenReturn("fake-jwt-token");

                // Act
                String token = authService.loginUser(payload);

                // Assert
                assertEquals("fake-jwt-token", token);

                verify(userRepository, times(1)).findByEmail("nelani@example.com");
        }

        @Test
        void loginUser_ShouldThrowException_WhenEmailIsInvalid() {
                // Arrange
                Map<String, String> payload = new HashMap<>();
                payload.put("email", "invalid-email");
                payload.put("password", "strongPassword@123");

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> authService.loginUser(payload));

                // Assert
                assertEquals("The provided email address is not valid. Please provide a valid email address.",
                                exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

        @Test
        void loginUser_ShouldThrowException_WhenPasswordIsInvalid() {
                // Arrange
                Map<String, String> payload = new HashMap<>();
                payload.put("email", "nelani@example.com");
                payload.put("password", "wrong-password");

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> authService.loginUser(payload));

                // Assert
                assertEquals("Password must contain at least 8 characters, an uppercase letter, a digit, and a special character.",
                                exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

        @Test
        void loginUser_ShouldThrowException_WhenEmailsDontMatch() {
                // Arrange
                Map<String, String> payload = new HashMap<>();
                payload.put("email", "maluka@example.com");
                payload.put("password", "strongPassword@123");

                // Fake user as if it's already in the database
                User existingUser = new User();
                existingUser.setEmail("nelani@example.com");
                existingUser.setPassword("encodedPassword123");
                existingUser.setProvider(Provider.LOCAL);

                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(existingUser));
                when(passwordEncoder.matches("strongPassword@123", "encodedPassword123"))
                                .thenReturn(true);

                // Act + Assert
                ValidationException exception = assertThrows(
                                ValidationException.class,
                                () -> authService.loginUser(payload));

                // Assert
                assertEquals("No user found with the provided email.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

        @Test
        void loginUser_ShouldThrowException_WhenPasswordsDontMatch() {
                // Arrange
                Map<String, String> payload = new HashMap<>();
                payload.put("email", "nelani@example.com");
                payload.put("password", "otherPassword@123");

                // Fake user as if it's already in the database
                User existingUser = new User();
                existingUser.setEmail("nelani@example.com");
                existingUser.setPassword("encodedPassword123");
                existingUser.setProvider(Provider.LOCAL);

                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(existingUser));
                when(passwordEncoder.matches("strongPassword@123", "encodedPassword123"))
                                .thenReturn(true);

                // Act + Assert
                ValidationException exception = assertThrows(
                                ValidationException.class,
                                () -> authService.loginUser(payload));

                // Assert
                assertEquals("Incorrect password.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

        @Test
        void loginUser_ShouldThrowException_WhenUserDoesNotExist() {
                // Arrange
                Map<String, String> payload = new HashMap<>();
                payload.put("email", "nelani@example.com");
                payload.put("password", "otherPassword@123");

                // Mock password encoder to match raw password
                when(passwordEncoder.matches("strongPassword@123", "encodedPassword123"))
                                .thenReturn(true);

                // Act + Assert
                ValidationException exception = assertThrows(
                                ValidationException.class,
                                () -> authService.loginUser(payload));

                // Assert
                assertEquals("No user found with the provided email.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

        @Test
        void loginUser_ShouldThrowException_WhenProviderInvalid() {
                // Arrange
                Map<String, String> payload = new HashMap<>();
                payload.put("email", "nelani@example.com");
                payload.put("password", "strongPassword@123");

                // Fake user as if it's already in the database
                User existingUser = new User();
                existingUser.setEmail("nelani@example.com");
                existingUser.setProvider(Provider.GOOGLE);

                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(existingUser));
                when(passwordEncoder.matches("strongPassword@123", "encodedPassword123"))
                                .thenReturn(true);

                // Act + Assert
                ValidationException exception = assertThrows(
                                ValidationException.class,
                                () -> authService.loginUser(payload));

                // Assert
                assertEquals("OAuth login required for this account.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

}
