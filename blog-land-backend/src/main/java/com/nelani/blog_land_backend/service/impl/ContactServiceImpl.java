package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Util.Validation.FormValidation;
import com.nelani.blog_land_backend.dto.ContactDto;
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
    public void getInfo(ContactDto contactDto) {
        // Validate fields
        String fullName = FormValidation.assertRequiredField(contactDto.getFullName(), "Full Name");
        String email = FormValidation.assertValidatedEmail(contactDto.getEmail());
        String message = FormValidation.assertRequiredField(contactDto.getMessage(), "Message");

        Contact contact = Contact.builder()
                .fullName(fullName)
                .email(email)
                .message(message)
                .build();

        contactRepository.save(contact); // Save the contact message
    }
}
