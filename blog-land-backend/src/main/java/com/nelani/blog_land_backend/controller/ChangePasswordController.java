package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.Util.ResponseBuilder;
import com.nelani.blog_land_backend.dto.PasswordDto;
import com.nelani.blog_land_backend.service.ChangePasswordService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/password")
public class ChangePasswordController {

    private final ChangePasswordService changePasswordService;

    public ChangePasswordController(ChangePasswordService changePasswordService) {
        this.changePasswordService = changePasswordService;
    }

    @PostMapping("/change")
    public ResponseEntity<?> changePassword(@RequestBody PasswordDto passwordDto) {
        try {
            return changePasswordService.changePasswordWithOldPassword(passwordDto);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.invalid("Validation Error", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }
}
