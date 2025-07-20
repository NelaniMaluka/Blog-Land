package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.Response.ErrorResponse;
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
    public ResponseEntity<?> getInfo (Contact contact){
        try{
            // trim and validate fields
            String fullname = FormValidation.trimAndValidate(contact.getFullname(), "Full Name");
            String email = FormValidation.trimAndValidate(contact.getEmail(), "Email");
            String message = FormValidation.trimAndValidate(contact.getMessage(), "Message");

            // Validate email format
            if (!FormValidation.isValidEmail(email)){
                return ResponseEntity.badRequest().body(new ErrorResponse("Invalid email format",
                        "The provided email address is not valid. Please provide a valid email address."));
            }

            contact.setFullname(fullname);
            contact.setEmail(email);
            contact.setMessage(message);

            // Save the contact message
            contactRepository.save(contact);
            return ResponseEntity.ok("Success, we received your message. Thank you for reaching out.");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal Server Error",
                            "An unexpected error occurred while saving your message. Please try again later."));
        }

    }
}
