package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.response.ErrorResponse;
import com.nelani.blog_land_backend.model.Newsletter;
import com.nelani.blog_land_backend.service.NewsletterService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/newsletter")
public class NewsletterController {

    private final NewsletterService newsletterService;

    public NewsletterController(NewsletterService newsletterService) {
        this.newsletterService = newsletterService;
    }

    @PostMapping()
    public ResponseEntity<?> addNewsletter(@RequestBody Newsletter newsletter) {
        try {
            return newsletterService.addEmail(newsletter);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse("Validation Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Internal Server Error",
                    "An unexpected error occurred while saving your message. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
