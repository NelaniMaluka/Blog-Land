package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.Validation.NewsletterValidation;
import com.nelani.blog_land_backend.dto.EmailDto;
import com.nelani.blog_land_backend.model.Newsletter;
import com.nelani.blog_land_backend.repository.NewsletterRepository;
import com.nelani.blog_land_backend.service.NewsletterService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NewsletterServiceImpl implements NewsletterService {

    private final NewsletterRepository newsletterRepository;
    private final NewsletterValidation newsletterValidation;

    public NewsletterServiceImpl(NewsletterRepository newsletterRepository, NewsletterValidation newsletterValidation) {
        this.newsletterRepository = newsletterRepository;
        this.newsletterValidation = newsletterValidation;
    }

    @Override
    @Transactional
    public void addEmail(EmailDto emailDto) {
        Newsletter newsletter = Newsletter.builder()
                .email(emailDto.getEmail())
                .build();

        // Check if the email is already subscribed
        newsletterValidation.assertEmailIsSubscribed(emailDto.getEmail());

        newsletterRepository.save(newsletter); // Save the newsletter email
    }
}
