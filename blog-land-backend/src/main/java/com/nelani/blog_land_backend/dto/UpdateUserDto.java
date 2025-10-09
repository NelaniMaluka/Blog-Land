package com.nelani.blog_land_backend.dto;

import com.nelani.blog_land_backend.model.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record UpdateUserDto(
                String title,
                String summary,

                @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,

                @NotBlank(message = "Firstname is required") String firstname,

                @NotBlank(message = "Lastname is required") String lastname,

                @NotNull(message = "Provider is required") Provider provider,

                String profileIconUrl,
                String location,
                ExperienceLevel experience,
                Map<String, String> socials) {
}
