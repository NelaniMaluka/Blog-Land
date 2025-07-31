package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.Validation.FormValidation;
import com.nelani.blog_land_backend.Util.Validation.UserValidation;
import com.nelani.blog_land_backend.config.JwtUtil;
import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.service.AuthService;

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

        public String registerUser(User user) {
                // Validate fields
                String email = FormValidation.assertValidatedEmail(user.getEmail());
                String firstname = FormValidation.assertRequiredField(user.getFirstname(), "Firstname");
                String lastname = FormValidation.assertRequiredField(user.getLastname(), "Lastname");
                String password = FormValidation.assertValidatedPassword(user.getPassword());

                // Checks if a user exists with the provided email
                Optional<User> optionalUser = userRepo.findByEmail(email);
                UserValidation.assertUserDoesNotExist(optionalUser, "User already exists with the provided email.");

                // Encodes the password and sets the provider to local
                user.setPassword(passwordEncoder.encode(password));
                user.setProvider(Provider.LOCAL);

                userRepo.save(user); // Saves the user

                return jwtUtils.generateJwtToken(user); // return jwt token
        }

        @Override
        public String loginUser(Map<String, String> payload) {
                SecurityContextHolder.clearContext();

                // Validate fields
                String email = FormValidation.assertValidatedEmail(payload.get("email"));
                String password = FormValidation.assertValidatedPassword(payload.get("password"));

                // Checks if a user doesn't exist with the provided email
                Optional<User> user = userRepo.findByEmail(email);
                UserValidation.assertUserExists(user, "Invalid email address.");

                // Checks if the user is local
                UserValidation.assertUserIsLocal(user.get(), "OAuth login required for this account.");

                // Checks if the passwords match
                UserValidation.assertUserPasswordsMatch(user.get(),passwordEncoder, password, "Incorrect password.");

                return jwtUtils.generateJwtToken(user.get()); // returns jwt token
        }
}
