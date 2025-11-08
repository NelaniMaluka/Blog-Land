package com.nelani.blog_land_backend.service.impl;

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
    public void addEmail(String email) {
        Newsletter newsletter = Newsletter.builder()
                .email(email)
                .build();

        // Check if the email is already subscribed
        if (newsletterRepository.findByEmail(email).isPresent()) {
            return;
        }

        newsletterRepository.save(newsletter); // Save the newsletter email
    }
}
