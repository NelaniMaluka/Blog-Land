package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.Util.JwtUtil;
import com.nelani.blog_land_backend.Util.Validation.ModerationValidator;
import com.nelani.blog_land_backend.model.ExperienceLevel;
import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.response.UserResponse;
import com.nelani.blog_land_backend.service.impl.UserServiceImpl;

import jakarta.validation.ValidationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceImplTest {

        @Mock
        private UserRepository userRepository;

        @Mock
        private ModerationValidator moderationValidator;

        @Mock
        private JwtUtil jwtUtils;

        @InjectMocks
        private UserServiceImpl userService;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
        }

        @Test
        void getUserDetails_ShouldReturnAuthenticatedUser() {
                // Arrange
                User existingUser = new User();
                existingUser.setEmail("nelani@example.com");
                existingUser.setFirstname("Nelani");
                existingUser.setLastname("Maluka");
                existingUser.setProvider(Provider.LOCAL);

                // Mock repository to return this user
                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(existingUser));

                // Set authenticated user in security context
                var auth = new UsernamePasswordAuthenticationToken(
                                existingUser, null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);

                // Act
                UserResponse result = userService.getUserDetails();

                // Assert
                assertNotNull(result);
                assertEquals("nelani@example.com", result.getEmail());
                assertEquals("Nelani", result.getFirstname());
                assertEquals("Maluka", result.getLastname());
                assertEquals(Provider.LOCAL, result.getProvider());
        }

        @Test
        void getUserDetails_ShouldThrowException_WhenNoAuthenticatedUser() {
                // Arrange
                SecurityContextHolder.clearContext();

                // Act + Assert
                BadCredentialsException exception = assertThrows(
                                BadCredentialsException.class,
                                () -> userService.getUserDetails());

                assertEquals("No authenticated user found.", exception.getMessage());
                verifyNoInteractions(userRepository);
        }

        @Test
        void updateUserDetails_ShouldSaveValidUser() {
                // Arrange
                User user = new User();
                user.setFirstname("Nelani");
                user.setLastname("Maluka");
                user.setEmail("nelani@example.com");
                user.setProvider(Provider.LOCAL);
                user.setLocation("Johannesburg ,South Africa");
                user.setExperience(ExperienceLevel.BEGINNER);

                Map<String, String> socials = new HashMap<>();
                socials.put("Instagram", "www.instagram");
                socials.put("github", "www.github");
                user.setSocials(socials);

                // Fake user as if it's already in the database
                User existingUser = new User();
                existingUser.setEmail("nelani@example.com");
                existingUser.setProvider(Provider.LOCAL);

                // Mock repository to find the user
                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(existingUser));
                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).userModeration(any(User.class));

                var auth = new UsernamePasswordAuthenticationToken(
                                existingUser, null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);

                // Act
                userService.updateUserDetails(user);

                // Assert
                ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
                verify(userRepository, times(1)).save(userCaptor.capture());
                verify(moderationValidator, times(1)).userModeration(user);

                User savedUser = userCaptor.getValue();
                assertEquals("Nelani", savedUser.getFirstname());
                assertEquals("Maluka", savedUser.getLastname());
                assertEquals("nelani@example.com", savedUser.getEmail());
                assertEquals(Provider.LOCAL, savedUser.getProvider());
                assertEquals("Johannesburg ,South Africa", user.getLocation());
                assertEquals(ExperienceLevel.BEGINNER, user.getExperience());
                assertEquals(socials, user.getSocials());
        }

        @Test
        void updateUserDetails_ShouldThrowException_WhenFirstnameIsEmpty() {
                // Arrange
                User user = new User();
                user.setFirstname("   ");
                user.setLastname("Maluka");
                user.setEmail("nelani@example.com");
                user.setProvider(Provider.LOCAL);
                user.setLocation("Johannesburg ,South Africa");
                user.setExperience(ExperienceLevel.BEGINNER);

                Map<String, String> socials = new HashMap<>();
                socials.put("Instagram", "www.instagram");
                socials.put("github", "www.github");
                user.setSocials(socials);

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> userService.updateUserDetails(user));

                assertEquals("Firstname is required.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

        @Test
        void updateUserDetails_ShouldThrowException_WhenLastnameIsEmpty() {
                // Arrange
                User user = new User();
                user.setFirstname("Nelani");
                user.setLastname("    ");
                user.setEmail("nelani@example.com");
                user.setProvider(Provider.LOCAL);
                user.setLocation("Johannesburg ,South Africa");
                user.setExperience(ExperienceLevel.BEGINNER);

                Map<String, String> socials = new HashMap<>();
                socials.put("Instagram", "www.instagram");
                socials.put("github", "www.github");
                user.setSocials(socials);

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> userService.updateUserDetails(user));

                assertEquals("Lastname is required.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

        @Test
        void updateUserDetails_ShouldThrowException_WhenEmailIsInvalid() {
                // Arrange
                User user = new User();
                user.setFirstname("Nelani");
                user.setLastname("Maluka");
                user.setEmail("invalid-email");
                user.setProvider(Provider.LOCAL);
                user.setLocation("Johannesburg ,South Africa");
                user.setExperience(ExperienceLevel.BEGINNER);

                Map<String, String> socials = new HashMap<>();
                socials.put("Instagram", "www.instagram");
                socials.put("github", "www.github");
                user.setSocials(socials);

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> userService.updateUserDetails(user));

                assertEquals("The provided email address is not valid. Please provide a valid email address.",
                                exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

        @Test
        void updateUserDetails_ShouldThrowException_WhenProviderIsInvalid() {
                // Arrange
                User user = new User();
                user.setFirstname("Nelani");
                user.setLastname("Maluka");
                user.setEmail("nelani@example.com");
                user.setProvider(Provider.GOOGLE);
                user.setLocation("Johannesburg ,South Africa");
                user.setExperience(ExperienceLevel.BEGINNER);

                Map<String, String> socials = new HashMap<>();
                socials.put("Instagram", "www.instagram");
                socials.put("github", "www.github");
                user.setSocials(socials);

                // Fake user as if it's already in the database
                User existingUser = new User();
                existingUser.setEmail("nelani@example.com");
                existingUser.setProvider(Provider.LOCAL);

                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(existingUser));
                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).userModeration(any(User.class));

                var auth = new UsernamePasswordAuthenticationToken(
                                existingUser, null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);

                // Act + Assert
                ValidationException exception = assertThrows(
                                ValidationException.class,
                                () -> userService.updateUserDetails(user));

                assertEquals("This account was registered with LOCAL . Please log in using your LOCAL provider.",
                                exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

        @Test
        void updateUserDetails_ShouldSave_WhenLocationIsEmpty() {
                // Arrange
                User user = new User();
                user.setFirstname("Nelani");
                user.setLastname("Maluka");
                user.setEmail("nelani@example.com");
                user.setProvider(Provider.LOCAL);
                user.setLocation("    ");
                user.setExperience(ExperienceLevel.BEGINNER);

                Map<String, String> socials = new HashMap<>();
                socials.put("Instagram", "www.instagram");
                socials.put("github", "www.github");
                user.setSocials(socials);

                // Fake user as if it's already in the database
                User existingUser = new User();
                existingUser.setEmail("nelani@example.com");
                existingUser.setProvider(Provider.LOCAL);

                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(existingUser));
                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).userModeration(any(User.class));

                var auth = new UsernamePasswordAuthenticationToken(
                                existingUser, null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);

                // Act
                userService.updateUserDetails(user);

                // Assert
                ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
                verify(userRepository, times(1)).save(userCaptor.capture());
                verify(moderationValidator, times(1)).userModeration(user);

                User savedUser = userCaptor.getValue();
                assertEquals("Nelani", savedUser.getFirstname());
                assertEquals("Maluka", savedUser.getLastname());
                assertEquals("nelani@example.com", savedUser.getEmail());
                assertEquals(Provider.LOCAL, savedUser.getProvider());
                assertEquals("    ", user.getLocation());
                assertEquals(ExperienceLevel.BEGINNER, user.getExperience());
                assertEquals(socials, user.getSocials());
        }

        @Test
        void updateUserDetails_ShouldSave_WhenExperienceIsEmpty() {
                // Arrange
                User user = new User();
                user.setFirstname("Nelani");
                user.setLastname("Maluka");
                user.setEmail("nelani@example.com");
                user.setProvider(Provider.LOCAL);
                user.setLocation("Johannesburg ,South Africa");
                user.setExperience(null);

                Map<String, String> socials = new HashMap<>();
                socials.put("Instagram", "www.instagram");
                socials.put("github", "www.github");
                user.setSocials(socials);

                // Fake user as if it's already in the database
                User existingUser = new User();
                existingUser.setEmail("nelani@example.com");
                existingUser.setProvider(Provider.LOCAL);

                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(existingUser));
                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).userModeration(any(User.class));

                var auth = new UsernamePasswordAuthenticationToken(
                                existingUser, null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);

                // Act
                userService.updateUserDetails(user);

                // Assert
                ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
                verify(userRepository, times(1)).save(userCaptor.capture());
                verify(moderationValidator, times(1)).userModeration(user);

                User savedUser = userCaptor.getValue();
                assertEquals("Nelani", savedUser.getFirstname());
                assertEquals("Maluka", savedUser.getLastname());
                assertEquals("nelani@example.com", savedUser.getEmail());
                assertEquals(Provider.LOCAL, savedUser.getProvider());
                assertEquals("Johannesburg ,South Africa", user.getLocation());
                assertNull(user.getExperience());
                assertEquals(socials, user.getSocials());
        }

        @Test
        void updateUserDetails_ShouldSave_WhenSocialsIsEmpty() {
                // Arrange
                User user = new User();
                user.setFirstname("Nelani");
                user.setLastname("Maluka");
                user.setEmail("nelani@example.com");
                user.setProvider(Provider.LOCAL);
                user.setLocation("Johannesburg ,South Africa");
                user.setExperience(ExperienceLevel.ADVANCED);
                user.setSocials(null);

                // Fake user as if it's already in the database
                User existingUser = new User();
                existingUser.setEmail("nelani@example.com");
                existingUser.setProvider(Provider.LOCAL);

                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(existingUser));
                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).userModeration(any(User.class));

                var auth = new UsernamePasswordAuthenticationToken(
                                existingUser, null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);

                // Act
                userService.updateUserDetails(user);

                // Assert
                ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
                verify(userRepository, times(1)).save(userCaptor.capture());
                verify(moderationValidator, times(1)).userModeration(user);

                User savedUser = userCaptor.getValue();
                assertEquals("Nelani", savedUser.getFirstname());
                assertEquals("Maluka", savedUser.getLastname());
                assertEquals("nelani@example.com", savedUser.getEmail());
                assertEquals(Provider.LOCAL, savedUser.getProvider());
                assertEquals("Johannesburg ,South Africa", user.getLocation());
                assertEquals(ExperienceLevel.ADVANCED, user.getExperience());
                assertNull(user.getSocials());
        }

        @Test
        void updateUserDetails_ShouldThrowException_WhenFailModeration() {
                // Arrange
                User user = new User();
                user.setFirstname("bitch");
                user.setLastname("fuck");
                user.setEmail("nelani@example.com");
                user.setProvider(Provider.LOCAL);
                user.setLocation("Johannesburg ,South Africa");
                user.setExperience(ExperienceLevel.ADVANCED);

                Map<String, String> socials = new HashMap<>();
                socials.put("Instagram", "www.instagram");
                socials.put("github", "www.github");
                user.setSocials(socials);

                // Fake user as if it's already in the database
                User existingUser = new User();
                existingUser.setEmail("nelani@example.com");
                existingUser.setProvider(Provider.LOCAL);

                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(existingUser));
                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doThrow(new ValidationException("User did not pass moderation."))
                                .when(moderationValidator).userModeration(any(User.class));

                var auth = new UsernamePasswordAuthenticationToken(
                                existingUser, null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);

                // Act + Assert
                ValidationException exception = assertThrows(
                                ValidationException.class,
                                () -> userService.updateUserDetails(user));

                assertEquals("User did not pass moderation.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

        @Test
        void updateUserDetails_ShouldThrowException_WhenUserDoesNotExist() {
                // Arrange
                User user = new User();
                user.setFirstname("Nelani");
                user.setLastname("Maluka");
                user.setEmail("nelani@example.com");
                user.setProvider(Provider.LOCAL);
                user.setLocation("Johannesburg ,South Africa");
                user.setExperience(ExperienceLevel.ADVANCED);

                Map<String, String> socials = new HashMap<>();
                socials.put("Instagram", "www.instagram");
                socials.put("github", "www.github");
                user.setSocials(socials);

                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).userModeration(any(User.class));

                // Act + Assert
                BadCredentialsException exception = assertThrows(
                                BadCredentialsException.class,
                                () -> userService.updateUserDetails(user));

                assertEquals("No authenticated user found.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).save(any());
        }

        @Test
        void deleteUserDetails_ShouldRemoveUser() {
                // Fake user as if it's already in the database
                User existingUser = new User();
                existingUser.setEmail("nelani@example.com");
                existingUser.setProvider(Provider.LOCAL);

                // Mock repository to find the user
                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(existingUser));

                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).userModeration(any(User.class));

                var auth = new UsernamePasswordAuthenticationToken(
                                existingUser, null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);

                // Act
                userService.deleteUserDetails();

                // Assert
                verify(userRepository, times(1)).delete(existingUser);
        }

        @Test
        void deleteUserDetails_ShouldThrowException_WhenNoAuthenticatedUser() {
                // Arrange
                SecurityContextHolder.clearContext();

                // Act + Assert
                BadCredentialsException exception = assertThrows(
                                BadCredentialsException.class,
                                () -> userService.deleteUserDetails());

                assertEquals("No authenticated user found.", exception.getMessage());

                // Ensure save is never called because it should fail
                verify(userRepository, never()).delete(any());
        }

}
