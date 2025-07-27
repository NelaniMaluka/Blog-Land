package com.nelani.blog_land_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordDto {
    private String oldPassword;
    private String newPassword;
    private String repeatPassword;
}


