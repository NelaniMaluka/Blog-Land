package com.nelani.blog_land_backend.dto;

import com.nelani.blog_land_backend.model.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDto {
    private String title;

    private String summary;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Firstname is required")
    private String firstname;

    @NotBlank(message = "Lastname is required")
    private String lastname;

    @NotNull(message = "Provider is required")
    private Provider provider;

    private String profileIconUrl;

    private String location;

    private ExperienceLevel experience;

    private Map<String, String> socials = new HashMap<>();
}
