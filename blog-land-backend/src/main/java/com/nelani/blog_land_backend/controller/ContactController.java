package com.nelani.blog_land_backend.controller;

import com.nelani.blog_land_backend.dto.ContactDto;
import com.nelani.blog_land_backend.service.ContactService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api")
@Tag(name = "Contact Controller", description = "Endpoints for managing contact form submissions or inquiries")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @Operation(summary = "Submit a contact message")
    @ApiResponse(responseCode = "201", description = "Message received successfully")
    @PostMapping("/public/contact")
    public ResponseEntity<String> getContactMessage(@RequestBody @Valid ContactDto contactDto) {
        contactService.getInfo(contactDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Success, we received your message. Thank you for reaching out.");
    }
}
