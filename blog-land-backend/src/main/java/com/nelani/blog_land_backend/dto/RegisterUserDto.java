package com.nelani.blog_land_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegisterUserDto(

                @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,

                @NotBlank(message = "Firstname is required") @Size(max = 50, message = "Firstname cannot exceed 50 characters") String firstname,

                @NotBlank(message = "Lastname is required") @Size(max = 50, message = "Lastname cannot exceed 50 characters") String lastname,

                @NotBlank(message = "Password is required") @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters") String password) {
}
