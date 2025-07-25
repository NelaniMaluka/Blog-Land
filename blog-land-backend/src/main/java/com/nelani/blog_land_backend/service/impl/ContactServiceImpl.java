package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.FormValidation;
import com.nelani.blog_land_backend.model.Contact;
import com.nelani.blog_land_backend.repository.ContactRepository;
import com.nelani.blog_land_backend.service.ContactService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<?> getInfo(Contact contact) {
        // trim and validate fields
        String fullname = FormValidation.trimAndValidate(contact.getFullname(), "Full Name");
        String email = FormValidation.validatedEmail(contact.getEmail());
        String message = FormValidation.trimAndValidate(contact.getMessage(), "Message");

        contact.setFullname(fullname);
        contact.setEmail(email);
        contact.setMessage(message);

        // Save the contact message
        contactRepository.save(contact);
        return ResponseEntity.status(HttpStatus.CREATED).body("Success, we received your message. Thank you for reaching out.");
    }
}
