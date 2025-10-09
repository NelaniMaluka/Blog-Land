package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.util.validation.ModerationValidator;
import com.nelani.blog_land_backend.util.validation.UserValidation;
import com.nelani.blog_land_backend.security.JwtUtil;
import com.nelani.blog_land_backend.dto.LoginUserDto;
import com.nelani.blog_land_backend.dto.RegisterUserDto;
import com.nelani.blog_land_backend.model.ExperienceLevel;
import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.service.AuthService;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

        private final ModerationValidator moderationValidator;
        private final UserRepository userRepo;
        private final PasswordEncoder passwordEncoder;
        private final UserValidation userValidation;
        private final JwtUtil jwtUtils;

        public AuthServiceImpl(ModerationValidator moderationValidator, UserRepository userRepo,
                        PasswordEncoder passwordEncoder, UserValidation userValidation, JwtUtil jwtUtils) {
                this.moderationValidator = moderationValidator;
                this.userRepo = userRepo;
                this.passwordEncoder = passwordEncoder;
                this.userValidation = userValidation;
                this.jwtUtils = jwtUtils;
        }

        @Override
        public String registerUser(RegisterUserDto user) {
                // Checks if a user exists with the provided email
                userValidation.assertUserDoesNotExist(user.email());

                // Encodes the password and sets the provider to local
                User newUser = User.builder()
                                .firstname(user.firstname())
                                .lastname(user.lastname())
                                .email(user.email())
                                .password(passwordEncoder.encode(user.password()))
                                .provider(Provider.LOCAL)
                                .experience(ExperienceLevel.NEW_BLOGGER)
                                .build();

                // Moderate content
                moderationValidator.userModeration(newUser);

                userRepo.save(newUser); // Saves the user

                return jwtUtils.generateJwtToken(newUser); // return jwt token
        }

        @Override
        public String loginUser(LoginUserDto loginUserDto) {
                SecurityContextHolder.clearContext();

                // Checks if a user doesn't exist with the provided email
                User existingUser = userValidation.assertUserExists(null, loginUserDto.email());

                // Checks if the user is local
                userValidation.assertUserIsLocal(existingUser, "OAuth login required for this account.");

                // Checks if the passwords match
                userValidation.assertEncodedPasswordsMatch(loginUserDto.password(), existingUser.getPassword());

                return jwtUtils.generateJwtToken(existingUser); // returns jwt token
        }
}
