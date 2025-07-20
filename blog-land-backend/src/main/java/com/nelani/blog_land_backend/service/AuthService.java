package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.model.User;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface AuthService {
    ResponseEntity<?> loginUser(Map<String, String> payload);
    ResponseEntity<?> registerUser(User user);
}
