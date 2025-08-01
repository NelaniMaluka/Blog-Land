package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.service.ForgotPasswordService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    public ForgotPasswordController(ForgotPasswordService forgotPasswordService) {
        this.forgotPasswordService = forgotPasswordService;
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> payload) {
            forgotPasswordService.requestPasswordReset(payload);
            return ResponseEntity.ok("Success, Password reset link sent to your email.");
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> payload) {
            forgotPasswordService.changePassword(payload);
            return ResponseEntity.ok("Success, Your password was changed successfully! You're all set.");
    }
}
