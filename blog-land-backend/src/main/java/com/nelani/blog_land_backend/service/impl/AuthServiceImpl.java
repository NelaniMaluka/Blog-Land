package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.FormValidation;
import com.nelani.blog_land_backend.Util.ResponseBuilder;
import com.nelani.blog_land_backend.config.JwtUtil;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.response.ErrorResponse;
import com.nelani.blog_land_backend.response.UserResponse;
import com.nelani.blog_land_backend.service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

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

            UserResponse userResponse = new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getFirstname(),
                    user.getLastname(),
                    user.getProvider(),
                    user.getProfileIconUrl(),
                    user.getLocation(),
                    token
            );

            return ResponseEntity.ok(userResponse);
    }

    @Override
    public ResponseEntity<?> loginUser(Map<String, String> payload) {
            // Clear security context Holder
            SecurityContextHolder.clearContext();

            // Trim and validate fields
            String email = FormValidation.validatedEmail(payload.get("email"));
            String password = FormValidation.validatedPassword(payload.get("password"));

            // Finds the user
            User user = userRepo.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            new ErrorResponse("Authentication Error",
                                    "Invalid credentials").toString()
                    ));

            // Validates user registration method
            if (!user.getProvider().equals("LOCAL")) {
                return ResponseBuilder.unauthorized("Authentication Method Error",
                        "OAuth login required for this account.");
            }

            // Validates user credentials
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return ResponseBuilder.unauthorized("Authentication Error",
                        "Invalid email or password combination.");
            }

            // Generate JwtToken
            String token = jwtUtils.generateJwtToken(user);

            UserResponse userResponse = new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getFirstname(),
                    user.getLastname(),
                    user.getProvider(),
                    user.getProfileIconUrl(),
                    user.getLocation(),
                    token
            );

            return ResponseEntity.ok(userResponse);
    }
}
