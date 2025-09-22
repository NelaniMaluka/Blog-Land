package com.nelani.blog_land_backend.service;

import java.util.Map;

public interface NewsletterService {
    void addEmail(Map<String, String> payload);
}
