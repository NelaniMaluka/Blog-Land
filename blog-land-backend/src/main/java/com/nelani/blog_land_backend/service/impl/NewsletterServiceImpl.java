package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.Validation.FormValidation;
import com.nelani.blog_land_backend.Util.Validation.NewsletterValidation;
import com.nelani.blog_land_backend.model.Newsletter;
import com.nelani.blog_land_backend.repository.NewsletterRepository;
import com.nelani.blog_land_backend.service.NewsletterService;

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
    public void addEmail(Newsletter newsletter) {
        // Validate fields
        String email = FormValidation.assertValidatedEmail(newsletter.getEmail());

        // Check if the email is already subscribed
        NewsletterValidation.assertEmailIsSubsribed(newsletterRepository, email);

        newsletterRepository.save(newsletter); // Save the newsletter email
    }
}
