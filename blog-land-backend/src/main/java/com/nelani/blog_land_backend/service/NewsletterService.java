package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.dto.EmailDto;

public interface NewsletterService {
    void addEmail(EmailDto emailDto);
}
