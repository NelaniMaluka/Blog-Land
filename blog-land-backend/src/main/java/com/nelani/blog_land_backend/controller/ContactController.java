package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.Util.Builders.ResponseBuilder;
import com.nelani.blog_land_backend.model.Contact;
import com.nelani.blog_land_backend.service.ContactService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact-us")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<?> getContactMessage(@RequestBody Contact contact) {
        try {
            contactService.getInfo(contact);
            return ResponseEntity.status(HttpStatus.CREATED).body("Success, we received your message. Thank you for reaching out.");
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.invalid("Validation Error", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.serverError();
        }
    }

}
