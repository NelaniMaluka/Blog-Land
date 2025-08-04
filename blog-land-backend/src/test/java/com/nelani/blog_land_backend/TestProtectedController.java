package com.nelani.blog_land_backend;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestProtectedController {

    @GetMapping("/some-protected-endpoint")
    public ResponseEntity<String> getProtectedResource() {
        return ResponseEntity.ok("You have accessed a protected resource!");
    }
}

