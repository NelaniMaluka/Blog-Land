package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.response.UserResponse;
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
            UserResponse userResponse = userService.getUserDetails();
            return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/update-user")
    public ResponseEntity<?> updateUseDetails(@RequestBody User user) {
            String newToken = userService.updateUserDetails(user);
            return ResponseEntity.ok(newToken);
    }

    @DeleteMapping("/delete-user")
    public ResponseEntity<?> deleteUseDetails() {
            userService.deleteUserDetails();
            return ResponseEntity.ok("Success, Successfully deleted your account");
    }

}
