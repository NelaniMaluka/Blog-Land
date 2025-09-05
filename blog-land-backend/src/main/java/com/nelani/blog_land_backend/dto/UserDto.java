package com.nelani.blog_land_backend.dto;

import com.nelani.blog_land_backend.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String title;
    private String summary;
    private String email;
    private String firstname;
    private String lastname;
    private Provider provider;
    private String profileIconUrl;
    private String location;
    private ExperienceLevel experience;
    private Map< String, String> socials = new HashMap<>();
}
