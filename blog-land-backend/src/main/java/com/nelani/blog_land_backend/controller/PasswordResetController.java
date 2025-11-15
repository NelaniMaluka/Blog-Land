package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.dto.ForgotPasswordDto;
import com.nelani.blog_land_backend.dto.PasswordDto;
import com.nelani.blog_land_backend.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api")
@Tag(name = "Password Reset Controller", description = "Endpoints for handling password reset requests and updates")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @Operation(summary = "Request a password reset link", description = "Send a password reset link to the user's email")
    @ApiResponse(responseCode = "201", description = "Success, password reset link sent")
    @PostMapping("/public/password/reset")
    public ResponseEntity<?> requestPasswordReset(
            @RequestParam @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email) {
        passwordResetService.requestPasswordReset(email);
        return ResponseEntity.status(HttpStatus.CREATED).body("Success, password reset link sent to your email.");
    }

    @Operation(summary = "Reset password using token", description = "Allows the user to reset their password using a reset token")
    @ApiResponse(responseCode = "200", description = "Success, password was changed successfully")
    @PutMapping("/public/password/reset/{token}")
    public ResponseEntity<?> resetPassword(
            @PathVariable String token,
            @RequestBody @Valid ForgotPasswordDto passwordDto) {
        passwordResetService.changePassword(passwordDto, token);
        return ResponseEntity.ok("Success, your password was changed successfully!");
    }

    @Operation(summary = "Change password for logged-in user", description = "Allows authenticated users to change their password by providing the old password")
    @ApiResponse(responseCode = "200", description = "Success, password was changed successfully")
    @PutMapping("/user/password")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> changePassword(@RequestBody @Valid PasswordDto passwordDto) {
        passwordResetService.changePasswordWithOldPassword(passwordDto);
        return ResponseEntity.ok("Success, your password was changed successfully!");
    }
}
