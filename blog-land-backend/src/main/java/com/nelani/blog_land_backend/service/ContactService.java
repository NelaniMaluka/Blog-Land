package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.model.Contact;
import org.springframework.http.ResponseEntity;

public interface ContactService {
    ResponseEntity<?> getInfo(Contact contact) throws Exception;
}
