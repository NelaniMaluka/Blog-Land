package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.dto.EmailDto;
import com.nelani.blog_land_backend.service.NewsletterService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/newsletter")
public class NewsletterController {

    private final NewsletterService newsletterService;

    public NewsletterController(NewsletterService newsletterService) {
        this.newsletterService = newsletterService;
    }

    @PostMapping()
    public ResponseEntity<?> addNewsletter(@RequestBody @Valid EmailDto emailDto) {
        newsletterService.addEmail(emailDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Success, we received your email. Thank you for subscribing to our newsletter.");
    }
}
