package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.model.Newsletter;
import com.nelani.blog_land_backend.service.NewsletterService;

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
    public ResponseEntity<?> addNewsletter(@RequestBody Newsletter newsletter) {
        newsletterService.addEmail(newsletter);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Success, we received your email. Thank you for subscribing to our newsletter.");
    }
}
