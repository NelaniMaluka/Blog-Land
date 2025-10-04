package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.dto.UpdateUserDto;
import com.nelani.blog_land_backend.response.UserResponse;
import com.nelani.blog_land_backend.response.UserSummaryAnalyticsResponse;
import com.nelani.blog_land_backend.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/upload-profile-image")
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("file") @NotNull(message = "File must be provided") MultipartFile file) {
        String response = userService.saveUserProfileImage(file);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove-profile-image")
    public ResponseEntity<?> removeProfileImage() {
        String response = userService.removeUserProfileImage();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-user")
    public ResponseEntity<?> getUserDetails() {
        UserResponse userResponse = userService.getUserDetails();
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/get/public-user-details/{nanoId}")
    public ResponseEntity<?> getPublicUserDetails(
            @PathVariable @NotBlank(message = "User ID cannot be blank") String nanoId) {

        UserResponse userResponse = userService.getPublicUserDetails(nanoId);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/get-user-summary-analytics")
    public ResponseEntity<?> getUseSummaryAnalytics() {
        UserSummaryAnalyticsResponse userResponse = userService.getUserSummaryAnalytics();
        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/update-user")
    public ResponseEntity<?> updateUseDetails(@RequestBody @Valid UpdateUserDto user) {
        String newToken = userService.updateUserDetails(user);
        return ResponseEntity.ok(newToken);
    }

    @DeleteMapping("/delete-user")
    public ResponseEntity<?> deleteUseDetails() {
        userService.deleteUserDetails();
        return ResponseEntity.ok("Success, Successfully deleted your account");
    }

    @PostMapping("/log-out")
    public ResponseEntity<?> logOut(HttpServletRequest request, HttpServletResponse response) {
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
