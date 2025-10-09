package com.nelani.blog_land_backend.service.impl;

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
        Contact contact = Contact.builder()
                .fullName(contactDto.fullName())
                .email(contactDto.email())
                .message(contactDto.message())
                .build();

        contactRepository.save(contact); // Save the contact message
    }
}
