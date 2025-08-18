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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
                SecurityContextHolder.clearContext(); // Clear context before each test
        }

        @Test
        void getUserDetails_ShouldReturnAuthenticatedUser() {
                // Arrange
                User existingUser = new User();
                existingUser.setEmail("nelani@example.com");
                existingUser.setFirstname("Nelani");
                existingUser.setLastname("Maluka");
                existingUser.setProvider(Provider.LOCAL);

                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(existingUser));

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
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
                SecurityContextHolder.clearContext(); // Ensure no authenticated user

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
                user.setLocation("Johannesburg, South Africa");
                user.setExperience(ExperienceLevel.CASUAL_POSTER);

                Map<String, String> socials = new HashMap<>();
                socials.put("Instagram", "www.instagram");
                socials.put("github", "www.github");
                user.setSocials(socials);

                User existingUser = new User();
                existingUser.setEmail("nelani@example.com");
                existingUser.setProvider(Provider.LOCAL);

                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(existingUser));
                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).userModeration(any(User.class));

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
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
                assertEquals("Johannesburg, South Africa", savedUser.getLocation());
                assertEquals(ExperienceLevel.CASUAL_POSTER, savedUser.getExperience());
                assertEquals(socials, savedUser.getSocials());
        }

        @Test
        void updateUserDetails_ShouldThrowException_WhenFirstnameIsEmpty() {
                // Arrange
                User user = new User();
                user.setFirstname("   ");
                user.setLastname("Maluka");
                user.setEmail("nelani@example.com");
                user.setProvider(Provider.LOCAL);
                user.setLocation("Johannesburg, South Africa");
                user.setExperience(ExperienceLevel.CASUAL_POSTER);

                Map<String, String> socials = new HashMap<>();
                socials.put("Instagram", "www.instagram");
                socials.put("github", "www.github");
                user.setSocials(socials);

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> userService.updateUserDetails(user));

                assertEquals("Firstname is required.", exception.getMessage());
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
                user.setLocation("Johannesburg, South Africa");
                user.setExperience(ExperienceLevel.CASUAL_POSTER);

                Map<String, String> socials = new HashMap<>();
                socials.put("Instagram", "www.instagram");
                socials.put("github", "www.github");
                user.setSocials(socials);

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> userService.updateUserDetails(user));

                assertEquals("Lastname is required.", exception.getMessage());
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
                user.setLocation("Johannesburg, South Africa");
                user.setExperience(ExperienceLevel.CASUAL_POSTER);

                Map<String, String> socials = new HashMap<>();
                socials.put("Instagram", "www.instagram");
                socials.put("github", "www.github");
                user.setSocials(socials);

                // Act + Assert
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> userService.updateUserDetails(user));

                assertEquals(
                                "The provided email address is not valid. Please provide a valid email address.",
                                exception.getMessage());
                verify(userRepository, never()).save(any());
        }

        @Test
        void updateUserDetails_ShouldThrowException_WhenProviderIsInvalid() {
                User user = new User();
                user.setFirstname("Nelani");
                user.setLastname("Maluka");
                user.setEmail("nelani@example.com");
                user.setProvider(Provider.GOOGLE);
                user.setLocation("Johannesburg, South Africa");
                user.setExperience(ExperienceLevel.CASUAL_POSTER);

                Map<String, String> socials = new HashMap<>();
                socials.put("Instagram", "www.instagram");
                socials.put("github", "www.github");
                user.setSocials(socials);

                User existingUser = new User();
                existingUser.setEmail("nelani@example.com");
                existingUser.setProvider(Provider.LOCAL);

                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(existingUser));
                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).userModeration(any(User.class));

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                existingUser, null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);

                ValidationException exception = assertThrows(
                                ValidationException.class,
                                () -> userService.updateUserDetails(user));

                assertEquals(
                                "This account was registered with LOCAL . Please log in using your LOCAL provider.",
                                exception.getMessage());

                verify(userRepository, never()).save(any());
        }

        @Test
        void updateUserDetails_ShouldSave_WhenLocationIsEmpty() {
                User user = new User();
                user.setFirstname("Nelani");
                user.setLastname("Maluka");
                user.setEmail("nelani@example.com");
                user.setProvider(Provider.LOCAL);
                user.setLocation("    ");
                user.setExperience(ExperienceLevel.CASUAL_POSTER);

                Map<String, String> socials = new HashMap<>();
                socials.put("Instagram", "www.instagram");
                socials.put("github", "www.github");
                user.setSocials(socials);

                User existingUser = new User();
                existingUser.setEmail("nelani@example.com");
                existingUser.setProvider(Provider.LOCAL);

                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(existingUser));
                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).userModeration(any(User.class));

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                existingUser, null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);

                userService.updateUserDetails(user);

                ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
                verify(userRepository, times(1)).save(userCaptor.capture());
                verify(moderationValidator, times(1)).userModeration(user);

                User savedUser = userCaptor.getValue();
                assertEquals("Nelani", savedUser.getFirstname());
                assertEquals("Maluka", savedUser.getLastname());
                assertEquals("nelani@example.com", savedUser.getEmail());
                assertEquals(Provider.LOCAL, savedUser.getProvider());
                assertEquals("    ", savedUser.getLocation());
                assertEquals(ExperienceLevel.CASUAL_POSTER, savedUser.getExperience());
                assertEquals(socials, savedUser.getSocials());
        }

        @Test
        void updateUserDetails_ShouldSave_WhenExperienceIsEmpty() {
                User user = new User();
                user.setFirstname("Nelani");
                user.setLastname("Maluka");
                user.setEmail("nelani@example.com");
                user.setProvider(Provider.LOCAL);
                user.setLocation("Johannesburg, South Africa");
                user.setExperience(null);

                Map<String, String> socials = new HashMap<>();
                socials.put("Instagram", "www.instagram");
                socials.put("github", "www.github");
                user.setSocials(socials);

                User existingUser = new User();
                existingUser.setEmail("nelani@example.com");
                existingUser.setProvider(Provider.LOCAL);

                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(existingUser));
                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).userModeration(any(User.class));

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                existingUser, null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);

                userService.updateUserDetails(user);

                ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
                verify(userRepository, times(1)).save(userCaptor.capture());
                verify(moderationValidator, times(1)).userModeration(user);

                User savedUser = userCaptor.getValue();
                assertEquals("Nelani", savedUser.getFirstname());
                assertEquals("Maluka", savedUser.getLastname());
                assertEquals("nelani@example.com", savedUser.getEmail());
                assertEquals(Provider.LOCAL, savedUser.getProvider());
                assertEquals("Johannesburg, South Africa", savedUser.getLocation());
                assertNull(savedUser.getExperience());
                assertEquals(socials, savedUser.getSocials());
        }

        @Test
        void updateUserDetails_ShouldSave_WhenSocialsIsEmpty() {
                User user = new User();
                user.setFirstname("Nelani");
                user.setLastname("Maluka");
                user.setEmail("nelani@example.com");
                user.setProvider(Provider.LOCAL);
                user.setLocation("Johannesburg, South Africa");
                user.setExperience(ExperienceLevel.CASUAL_POSTER);
                user.setSocials(null);

                User existingUser = new User();
                existingUser.setEmail("nelani@example.com");
                existingUser.setProvider(Provider.LOCAL);

                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(existingUser));
                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).userModeration(any(User.class));

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                existingUser, null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);

                userService.updateUserDetails(user);

                ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
                verify(userRepository, times(1)).save(userCaptor.capture());
                verify(moderationValidator, times(1)).userModeration(user);

                User savedUser = userCaptor.getValue();
                assertEquals("Nelani", savedUser.getFirstname());
                assertEquals("Maluka", savedUser.getLastname());
                assertEquals("nelani@example.com", savedUser.getEmail());
                assertEquals(Provider.LOCAL, savedUser.getProvider());
                assertEquals("Johannesburg, South Africa", savedUser.getLocation());
                assertEquals(ExperienceLevel.CASUAL_POSTER, savedUser.getExperience());
                assertNull(savedUser.getSocials());
        }

        @Test
        void updateUserDetails_ShouldThrowException_WhenFailModeration() {
                User user = new User();
                user.setFirstname("bitch");
                user.setLastname("fuck");
                user.setEmail("nelani@example.com");
                user.setProvider(Provider.LOCAL);
                user.setLocation("Johannesburg, South Africa");
                user.setExperience(ExperienceLevel.CASUAL_POSTER);

                Map<String, String> socials = new HashMap<>();
                socials.put("Instagram", "www.instagram");
                socials.put("github", "www.github");
                user.setSocials(socials);

                User existingUser = new User();
                existingUser.setEmail("nelani@example.com");
                existingUser.setProvider(Provider.LOCAL);

                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(existingUser));
                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doThrow(new ValidationException("User did not pass moderation."))
                                .when(moderationValidator).userModeration(any(User.class));

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                existingUser, null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);

                ValidationException exception = assertThrows(
                                ValidationException.class,
                                () -> userService.updateUserDetails(user));

                assertEquals("User did not pass moderation.", exception.getMessage());
                verify(userRepository, never()).save(any());
        }

        @Test
        void updateUserDetails_ShouldThrowException_WhenUserDoesNotExist() {

                when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

                SecurityContext securityContext = Mockito.mock(SecurityContext.class);
                when(securityContext.getAuthentication()).thenReturn(null);
                SecurityContextHolder.setContext(securityContext);

                User userUpdate = new User();
                userUpdate.setFirstname("Nelani");
                userUpdate.setLastname("Maluka");
                userUpdate.setEmail("nelani@example.com");
                userUpdate.setProvider(Provider.LOCAL);

                BadCredentialsException exception = assertThrows(
                                BadCredentialsException.class,
                                () -> userService.updateUserDetails(userUpdate));

                assertEquals("No authenticated user found.", exception.getMessage());
                verify(userRepository, never()).save(any());
        }

        @Test
        void deleteUserDetails_ShouldRemoveUser() {
                User existingUser = new User();
                existingUser.setEmail("nelani@example.com");
                existingUser.setProvider(Provider.LOCAL);

                when(userRepository.findByEmail("nelani@example.com"))
                                .thenReturn(Optional.of(existingUser));
                when(jwtUtils.generateJwtToken(any(User.class))).thenReturn("fake-jwt-token");
                doNothing().when(moderationValidator).userModeration(any(User.class));

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                existingUser, null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);

                userService.deleteUserDetails();

                verify(userRepository, times(1)).delete(existingUser);
        }

        @Test
        void deleteUserDetails_ShouldThrowException_WhenNoAuthenticatedUser() {
                SecurityContextHolder.clearContext();

                BadCredentialsException exception = assertThrows(
                                BadCredentialsException.class,
                                () -> userService.deleteUserDetails());

                assertEquals("No authenticated user found.", exception.getMessage());
                verify(userRepository, never()).delete(any());
        }
}
