package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.ResponseBuilder;
import com.nelani.blog_land_backend.Util.FormValidation;
import com.nelani.blog_land_backend.model.Newsletter;
import com.nelani.blog_land_backend.repository.NewsletterRepository;
import com.nelani.blog_land_backend.service.NewsletterService;

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
    public ResponseEntity<?> addEmail(Newsletter newsletter) {
        // trim and validate fields
        String email = FormValidation.validatedEmail(newsletter.getEmail());

        // Check if email is already subscribed
        if (newsletterRepository.findByEmail(email).isPresent()) {
            return ResponseBuilder.invalid("Invalid Request",
                    "The provided email is already subscribed to our newsletter");
        }
        ;

        newsletter.setEmail(email);

        // Save the newsletter email
        newsletterRepository.save(newsletter);
        return ResponseEntity.ok("Success, we received your email. Thank you for subscribing to our newsletter");
    }

}
