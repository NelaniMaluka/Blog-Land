package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.dto.PasswordDto;
import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.nelani.blog_land_backend.service.impl.ChangePasswordServiceImpl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

public class ChangePasswordServiceImpleTest {


    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChangePasswordServiceImpl changePasswordService;

@Mock
private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Arrange
        User existingUser = new User();
        existingUser.setEmail("nelani@example.com");
        existingUser.setFirstname("Nelani");
        existingUser.setLastname("Maluka");

        // Explicitly set encoded password
        existingUser.setPassword("encodedStrongPassword@123");

        existingUser.setProvider(Provider.LOCAL);
        existingUser.setId(100L);

        when(userRepository.findByEmail("nelani@example.com"))
                .thenReturn(Optional.of(existingUser));

        var auth = new UsernamePasswordAuthenticationToken(
                existingUser, null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void changePasswordWithOldPassword_ShouldChangePassword() {
        // Arrange
        PasswordDto credentials = new PasswordDto();
        credentials.setOldPassword("strongPassword@123");
        credentials.setNewPassword("strongPassword@1234");
        credentials.setRepeatPassword("strongPassword@1234");

        // Mock password matching and encoding
        when(passwordEncoder.matches("strongPassword@123", "encodedStrongPassword@123"))
                .thenReturn(true);
        when(passwordEncoder.encode("strongPassword@1234"))
                .thenReturn("encodedStrongPassword@1234");

        // Act
        changePasswordService.changePasswordWithOldPassword(credentials);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("encodedStrongPassword@1234", savedUser.getPassword());
    }

    @Test
    void changePasswordWithOldPassword_ShouldThrowException_WhenPasswordsMatch() {
        // Arrange
        PasswordDto credentials = new PasswordDto();
        credentials.setOldPassword("strongPassword@123");
        credentials.setNewPassword("strongPassword@123");
        credentials.setRepeatPassword("strongPassword@123");

        // Mock password matching and encoding
        when(passwordEncoder.matches("strongPassword@123", "encodedStrongPassword@123"))
                .thenReturn(true);

        // Act + Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> changePasswordService.changePasswordWithOldPassword(credentials));

        assertEquals("You cannot reuse your current password. Please choose a new password.", exception.getMessage());

        // Ensure save is never called because it should fail
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePasswordWithOldPassword_ShouldThrowException_WhenOldPasswordsEmpty() {
        // Arrange
        PasswordDto credentials = new PasswordDto();
        credentials.setOldPassword("    ");
        credentials.setNewPassword("strongPassword@123");
        credentials.setRepeatPassword("strongPassword@123");

        // Act + Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> changePasswordService.changePasswordWithOldPassword(credentials));

        assertEquals("Password is required.", exception.getMessage());

        // Ensure save is never called because it should fail
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePasswordWithOldPassword_ShouldThrowException_WhenNewPasswordsEmpty() {
        // Arrange
        PasswordDto credentials = new PasswordDto();
        credentials.setOldPassword("strongPassword@123");
        credentials.setNewPassword("   ");
        credentials.setRepeatPassword("strongPassword@123");

        // Act + Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> changePasswordService.changePasswordWithOldPassword(credentials));

        assertEquals("Password is required.", exception.getMessage());

        // Ensure save is never called because it should fail
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePasswordWithOldPassword_ShouldThrowException_WhenRepeatPasswordsEmpty() {
        // Arrange
        PasswordDto credentials = new PasswordDto();
        credentials.setOldPassword("strongPassword@123");
        credentials.setNewPassword("strongPassword@123");
        credentials.setRepeatPassword("   ");

        // Act + Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> changePasswordService.changePasswordWithOldPassword(credentials));

        assertEquals("Password is required.", exception.getMessage());

        // Ensure save is never called because it should fail
        verify(userRepository, never()).save(any());
    }

}
