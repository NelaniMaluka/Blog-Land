package com.nelani.blog_land_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailDto(
        @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email) {
}
