package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.dto.UpdateUserDto;
import com.nelani.blog_land_backend.response.LoginResponse;
import com.nelani.blog_land_backend.response.UserResponse;
import com.nelani.blog_land_backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("/api")
@Tag(name = "User Controller", description = "Endpoints for managing authenticated user actions and data")
public class UserController {

        private final UserService userService;

        public UserController(UserService userService) {
                this.userService = userService;
        }

        @PostMapping("/user/image/upload")
        @Operation(summary = "Upload a profile image", description = "Allows the authenticated user to upload a profile image. "
                        +
                        "The uploaded file must be provided as a multipart/form-data request.")
        @ApiResponse(responseCode = "200", description = "Successfully uploaded image")
        @PreAuthorize("hasAuthority('user:write')")
        public ResponseEntity<String> uploadProfileImage(
                        @RequestParam("file") @NotNull(message = "File must be provided") MultipartFile file) {
                userService.saveUserProfileImage(file);
                return ResponseEntity.ok("Successfully uploaded image");
        }

        @DeleteMapping("/user/image/remove")
        @Operation(summary = "Remove profile image", description = "Removes the profile image of the authenticated user. "
                        +
                        "This endpoint does not return any content.")
        @ApiResponse(responseCode = "204", description = "Successfully removed profile image, no content returned")
        @PreAuthorize("hasAuthority('user:write')")
        public ResponseEntity<Void> removeProfileImage() {
                userService.removeUserProfileImage();
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/public/user/{nanoId}")
        @Operation(summary = "Get public user details", description = "Retrieves publicly visible details of a user by their unique Nano ID.")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved public user details", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)))
        public ResponseEntity<UserResponse> getPublicUserDetails(
                        @PathVariable @NotBlank(message = "User ID cannot be blank") String nanoId) {
                UserResponse userResponse = userService.getPublicUserDetails(nanoId);

                return ResponseEntity.ok(userResponse);
        }

        @GetMapping("/user/me")
        @Operation(summary = "Get authenticated user details", description = "Retrieves the details of the currently authenticated user.")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user details", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)))
        @PreAuthorize("hasAuthority('user:read')")
        public ResponseEntity<UserResponse> getUserDetails() {
                UserResponse userResponse = userService.getUserDetails();
                return ResponseEntity.ok(userResponse);
        }

        @PutMapping("/user/update")
        @Operation(summary = "Update authenticated user details", description = "Updates the details of the currently authenticated user using the provided information.")
        @ApiResponse(responseCode = "200", description = "Successfully updated user details", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class)))
        @PreAuthorize("hasAuthority('user:write')")
        public ResponseEntity<LoginResponse> updateUseDetails(
                        @RequestBody @Valid UpdateUserDto user) {
                LoginResponse response = userService.updateUserDetails(user);
                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/user/remove")
        @Operation(summary = "Delete authenticated user account", description = "Deletes the currently authenticated user's account. "
                        +
                        "This endpoint does not return any content.")
        @ApiResponse(responseCode = "204", description = "Successfully deleted user account, no content returned")
        @PreAuthorize("hasAuthority('user:delete')")
        public ResponseEntity<Void> deleteUseDetails() {
                userService.deleteUserDetails();
                return ResponseEntity.noContent().build();
        }

}
