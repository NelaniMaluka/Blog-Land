package com.nelani.blog_land_backend.dto;

import lombok.Data;

@Data
public class ContactDto {
    private String fullName;
    private String email;
    private String message;
}
