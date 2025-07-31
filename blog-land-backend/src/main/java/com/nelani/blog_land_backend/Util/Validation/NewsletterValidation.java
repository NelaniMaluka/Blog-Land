package com.nelani.blog_land_backend.Util.Validation;

import com.nelani.blog_land_backend.repository.NewsletterRepository;

public class NewsletterValidation {

    public static void assertEmailIsSubsribed(NewsletterRepository newsletterRepository, String email){
        if (newsletterRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("The provided email is already subscribed to our newsletter");
        };
    }

}
