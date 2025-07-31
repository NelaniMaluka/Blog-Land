package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.Validation.FormValidation;
import com.nelani.blog_land_backend.model.Contact;
import com.nelani.blog_land_backend.repository.ContactRepository;
import com.nelani.blog_land_backend.service.ContactService;

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
    public void getInfo(Contact contact) {
        // Validate fields
        String fullName = FormValidation.assertRequiredField(contact.getFullName(), "Full Name");
        String email = FormValidation.assertValidatedEmail(contact.getEmail());
        String message = FormValidation.assertRequiredField(contact.getMessage(), "Message");

        contactRepository.save(contact); // Save the contact message
    }
}
