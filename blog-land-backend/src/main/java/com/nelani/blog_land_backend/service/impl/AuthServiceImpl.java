package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.FormValidation;
import com.nelani.blog_land_backend.Util.ResponseBuilder;
import com.nelani.blog_land_backend.config.JwtUtil;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

        private final UserRepository userRepo;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtil jwtUtils;

        public AuthServiceImpl(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtils) {
                this.userRepo = userRepo;
                this.passwordEncoder = passwordEncoder;
                this.jwtUtils = jwtUtils;
        }

        @Override
        public ResponseEntity<?> registerUser(User user) {
                // Trim and validate fields
                String email = FormValidation.validatedEmail(user.getEmail());
                String firstname = FormValidation.trimAndValidate(user.getFirstname(), "Firstname");
                String lastname = FormValidation.trimAndValidate(user.getLastname(), "Lastname");
                String password = FormValidation.validatedPassword(user.getPassword());

                // Checks if a user exists with that email
                if (userRepo.findByEmail(email).isPresent()) {
                        return ResponseBuilder.invalid("User Creation Error",
                                        "User already exists with the provided username.");
                }

                // Encodes the password and saves it to the database
                user.setPassword(passwordEncoder.encode(password));
                user.setProvider("LOCAL");
                userRepo.save(user);

                // Generates Jwt token
                String token = jwtUtils.generateJwtToken(user);

                return ResponseEntity.ok(token);
        }

        @Override
        public ResponseEntity<?> loginUser(Map<String, String> payload) {
                // Clear security context Holder
                SecurityContextHolder.clearContext();

                // Trim and validate fields
                String email = FormValidation.validatedEmail(payload.get("email"));
                String password = FormValidation.validatedPassword(payload.get("password"));

                // Finds the user
                Optional<User> optionalUser = userRepo.findByEmail(email);
                if (optionalUser.isEmpty()) {
                        return ResponseBuilder.unauthorized("Login Failed",
                                        "The email you entered is incorrect. Please try again.");
                }

                User user = optionalUser.get();

                // Validates user registration method
                if (!user.getProvider().equals("LOCAL")) {
                        return ResponseBuilder.unauthorized("Authentication Method Error",
                                        "OAuth login required for this account.");
                }

                // Validates user credentials
                if (!passwordEncoder.matches(password, user.getPassword())) {
                        return ResponseBuilder.unauthorized("Login Failed",
                                        "The password you entered is incorrect. Please try again.");
                }

                // Generate JwtToken
                String token = jwtUtils.generateJwtToken(user);

                return ResponseEntity.status(HttpStatus.CREATED).body(token);
        }
}
