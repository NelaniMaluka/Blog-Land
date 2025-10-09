package com.nelani.blog_land_backend.util.validation;

import com.nelani.blog_land_backend.repository.NewsletterRepository;
import org.springframework.stereotype.Component;

@Component
public class NewsletterValidation {
    private final NewsletterRepository newsletterRepository;

    public NewsletterValidation(NewsletterRepository newsletterRepository) {
        this.newsletterRepository = newsletterRepository;
    }

    public void assertEmailIsSubscribed(String email){
        if (newsletterRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("The provided email is already subscribed to our newsletter");
        };
    }

}
