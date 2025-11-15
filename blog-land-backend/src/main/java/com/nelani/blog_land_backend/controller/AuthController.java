package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.dto.LoginUserDto;
import com.nelani.blog_land_backend.dto.RegisterUserDto;
import com.nelani.blog_land_backend.response.LoginResponse;
import com.nelani.blog_land_backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api")
@Tag(name = "Authentication Controller", description = "Endpoints for user registration and login")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register a new user", description = "Creates a new user account and returns an authentication token.")
    @ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class)))
    @PostMapping("/public/auth/register")
    public ResponseEntity<LoginResponse> register(@RequestBody @Valid RegisterUserDto user) {
        LoginResponse response = authService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Login an existing user", description = "Authenticates a user and returns a JWT token.")
    @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class)))
    @PostMapping("/public/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginUserDto user) {
        LoginResponse response = authService.loginUser(user);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Log out the authenticated user", description = "Logs out the currently authenticated user by clearing the security context "
            +
            "and invalidating the session. Returns a success message upon completion.")
    @ApiResponse(responseCode = "200", description = "User successfully logged out")
    @PostMapping("/user/auth/log-out")
    public ResponseEntity<String> logOut(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authHeader.substring(7);
        } else {
            throw new RuntimeException("No Authorization header provided or token is missing.");
        }

        return ResponseEntity.ok("Logged out successfully");
    }

}
