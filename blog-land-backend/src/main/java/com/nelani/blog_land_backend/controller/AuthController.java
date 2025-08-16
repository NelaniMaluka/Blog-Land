package com.nelani.blog_land_backend.controller;

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
        String token = authService.registerUser(user);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String token = authService.loginUser(payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

}
