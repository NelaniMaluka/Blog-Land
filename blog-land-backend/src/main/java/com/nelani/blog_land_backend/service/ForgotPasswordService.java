package com.nelani.blog_land_backend.service;

import org.springframework.http.ResponseEntity;
import java.util.Map;

public interface ForgotPasswordService {
    ResponseEntity<?> requestPasswordReset( Map<String, String> payload);
    ResponseEntity<?> changePassword( Map<String, String> payload);
}
