package com.nelani.blog_land_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record PasswordDto(

                @NotBlank(message = "Old password cannot be empty") String oldPassword,

                @NotBlank(message = "New password cannot be empty") @Size(min = 8, max = 64, message = "New password must be between 8 and 64 characters") String newPassword,

                @NotBlank(message = "Repeat password cannot be empty") String repeatPassword) {
}
