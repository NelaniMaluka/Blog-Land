package com.nelani.blog_land_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactDto(

        @NotBlank(message = "Full name is required") @Size(max = 100, message = "Full name cannot exceed 100 characters") String fullName,

        @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,

        @NotBlank(message = "Message cannot be empty") @Size(max = 500, message = "Message cannot exceed 500 characters") String message) {
}
