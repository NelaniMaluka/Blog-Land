package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Response.ErrorResponse;
import com.nelani.blog_land_backend.Util.FormValidation;
import com.nelani.blog_land_backend.model.Newsletter;
import com.nelani.blog_land_backend.repository.NewsletterRepository;
import com.nelani.blog_land_backend.service.NewsletterService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NewsletterServiceImpl implements NewsletterService {

    private final NewsletterRepository newsletterRepository;

    public NewsletterServiceImpl(NewsletterRepository newsletterRepository) {
        this.newsletterRepository = newsletterRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<?> addEmail(Newsletter newsletter){
        try {
            // trim and validate fields
            String email = FormValidation.trimAndValidate(newsletter.getEmail(), "Email");

            // Validate email format
            if (!FormValidation.isValidEmail(email)){
                return ResponseEntity.badRequest().body(new ErrorResponse("Invalid email format",
                        "The provided email address is not valid. Please provide a valid email address."));
            }

            // Check if email is already subscribed
            if (newsletterRepository.findByEmail(email).isPresent()){
                return ResponseEntity.badRequest().body(new ErrorResponse("Invalid Request",
                        "The provided email is already subscribed to our newsletter"));
            };

            newsletter.setEmail(email);

            // Save the newsletter email
            newsletterRepository.save(newsletter);
            return ResponseEntity.ok("Success, we received your email. Thank you for subscribing to our newsletter");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal Server Error",
                            "An unexpected error occurred while saving your message. Please try again later."));
        }
    }

}
