package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.model.Newsletter;
import org.springframework.http.ResponseEntity;

public interface NewsletterService {
    ResponseEntity<?> addEmail(Newsletter newsletter) throws Exception;
}
