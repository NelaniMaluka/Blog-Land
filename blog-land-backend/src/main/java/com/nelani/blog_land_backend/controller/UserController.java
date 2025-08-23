package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.response.UserResponse;
import com.nelani.blog_land_backend.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final RedisTemplate<String, String> redisTemplate;
    private final long tokenExpirySeconds = 24 * 60 * 60;

    public UserController(UserService userService, RedisTemplate<String, String> redisTemplate) {
        this.userService = userService;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/get-user")
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

    @PostMapping("/log-out")
    public ResponseEntity<?> logOut(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.substring(7);
            redisTemplate.opsForValue()
                    .set(jwtToken, "blacklisted", tokenExpirySeconds, TimeUnit.SECONDS);
        } else {
            throw new RuntimeException("No Authorization header provided or token is missing.");
        } 

        return ResponseEntity.ok("Logged out successfully");
    }
}
