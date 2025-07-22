package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.dto.PasswordDto;
import com.nelani.blog_land_backend.response.ErrorResponse;
import com.nelani.blog_land_backend.service.PasswordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/password")
public class PasswordController {

    private final PasswordService passwordService;

    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @PostMapping("/change")
    public ResponseEntity<?> changePassword(@RequestBody PasswordDto passwordDto) {
        try {
            return passwordService.changePasswordWithOldPassword(passwordDto);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse("Validation Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Internal Server Error",
                    "An unexpected error occurred while saving your message. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
