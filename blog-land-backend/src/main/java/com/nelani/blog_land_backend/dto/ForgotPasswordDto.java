package com.nelani.blog_land_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ForgotPasswordDto(

        @NotBlank(message = "New password is required") @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters") String newPassword,

        @NotBlank(message = "Repeat password is required") String repeatPassword) {
}
