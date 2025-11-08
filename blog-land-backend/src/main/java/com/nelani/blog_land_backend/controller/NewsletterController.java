package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.service.NewsletterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api")
@Tag(name = "Newsletter Controller", description = "Handles newsletter subscriptions.")
public class NewsletterController {

    private final NewsletterService newsletterService;

    public NewsletterController(NewsletterService newsletterService) {
        this.newsletterService = newsletterService;
    }

    @Operation(summary = "Subscribe to the newsletter", description = "Registers a user email to receive newsletter updates.")
    @ApiResponse(responseCode = "201", description = "Successfully subscribed to newsletter", content = @Content(schema = @Schema(example = "{\"message\": \"Success, we received your email. Thank you for subscribing to our newsletter.\"}")))
    @PostMapping("/public/newsletter")
    public ResponseEntity<?> addNewsletter(
            @RequestParam @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email) {

        newsletterService.addEmail(email);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Success, we received your email. Thank you for subscribing to our newsletter.");
    }
}
