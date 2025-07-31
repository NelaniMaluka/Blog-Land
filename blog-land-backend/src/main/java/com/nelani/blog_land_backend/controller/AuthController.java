package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.Util.Builders.ResponseBuilder;
import com.nelani.blog_land_backend.response.ErrorResponse;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            String token = authService.registerUser(user);
            return ResponseEntity.ok(token);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse("Validation Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        try {
            String token = authService.loginUser(payload);
            return ResponseEntity.status(HttpStatus.CREATED).body(token);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.unauthorized("Login Failed", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }
}
