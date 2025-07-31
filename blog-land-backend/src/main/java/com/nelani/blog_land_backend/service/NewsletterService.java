package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.model.Newsletter;

public interface NewsletterService {
    void addEmail(Newsletter newsletter) throws Exception;
}
