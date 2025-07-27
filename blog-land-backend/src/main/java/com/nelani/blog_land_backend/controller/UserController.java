package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.Util.ResponseBuilder;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<?> getUseDetails() {
        try {
            return userService.getUserDetails();
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.invalid("Validation Error", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

    @PutMapping("/update-user")
    public ResponseEntity<?> updateUseDetails(@RequestBody User user) {
        try {
            return userService.updateUserDetails(user);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.invalid("Validation Error", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

    @DeleteMapping("/delete-user")
    public ResponseEntity<?> deleteUseDetails(@RequestParam Long id) {
        try {
            return userService.deleteUserDetails(id);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.invalid("Validation Error", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

}
