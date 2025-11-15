package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.mapper.UserMapper;
import com.nelani.blog_land_backend.repository.UserSocialRepository;
import com.nelani.blog_land_backend.response.LoginResponse;
import com.nelani.blog_land_backend.security.JwtService;
import com.nelani.blog_land_backend.util.validation.ModerationValidator;
import com.nelani.blog_land_backend.dto.LoginUserDto;
import com.nelani.blog_land_backend.dto.RegisterUserDto;
import com.nelani.blog_land_backend.model.ExperienceLevel;
import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.service.AuthService;

import jakarta.validation.ValidationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Map;

@Log4j2
@Service
public class AuthServiceImpl implements AuthService {

        private final ModerationValidator moderationValidator;
        private final UserRepository userRepo;
        private final UserSocialRepository userSocialRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;

        public AuthServiceImpl(ModerationValidator moderationValidator, UserRepository userRepo,
                        UserSocialRepository userSocialRepository, PasswordEncoder passwordEncoder,
                        JwtService jwtService) {
                this.moderationValidator = moderationValidator;
                this.userRepo = userRepo;
                this.userSocialRepository = userSocialRepository;
                this.passwordEncoder = passwordEncoder;
                this.jwtService = jwtService;
        }

        @Override
        @Transactional
        public LoginResponse registerUser(RegisterUserDto user) {
                log.info("Registering new user with email: {}", user.email());

                // Check if user already exists
                userRepo.findByEmail(user.email()).ifPresent(u -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT,
                                        "User already exists with the provided email.");
                });

                // Build and encode user
                User newUser = User.builder()
                                .firstname(user.firstname())
                                .lastname(user.lastname())
                                .email(user.email())
                                .password(passwordEncoder.encode(user.password()))
                                .provider(Provider.LOCAL)
                                .experience(ExperienceLevel.NEW_BLOGGER)
                                .build();

                // Moderate and save
                moderationValidator.userModeration(newUser, Map.of());
                userRepo.save(newUser);

                // Generate JWT
                String token = jwtService.generateToken(newUser);

                log.info("User registered successfully: {}", newUser.getEmail());

                return LoginResponse.builder()
                                .token(token)
                                .expiresIn(86400000)
                                .user(UserMapper.buildLoggedInUser(newUser, Collections.emptyList()))
                                .build();
        }

        @Override
        @Transactional
        public LoginResponse loginUser(LoginUserDto loginUserDto) {
                log.info("Login attempt for email: {}", loginUserDto.email());

                // Check if user exists
                User existingUser = userRepo.findByEmail(loginUserDto.email())
                                .orElseThrow(() -> new ValidationException(
                                                "No account is associated with that email."));

                // Check provider type
                if (!existingUser.getProvider().equals(Provider.LOCAL)) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                                        "OAuth login required for this account.");
                }

                // Validate password
                if (!passwordEncoder.matches(loginUserDto.password(), existingUser.getPassword())) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials provided.");
                }

                // Fetch socials
                var socials = userSocialRepository.findByUser(existingUser);

                // Generate JWT token
                String token = jwtService.generateToken(existingUser);

                log.info("User logged in successfully: {}", existingUser.getEmail());

                return LoginResponse.builder()
                                .token(token)
                                .expiresIn(86400000)
                                .user(UserMapper.buildLoggedInUser(existingUser, socials))
                                .build();
        }

}
