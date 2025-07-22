package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.dto.PasswordDto;
import org.springframework.http.ResponseEntity;

public interface PasswordService {
    ResponseEntity<?> changePasswordWithOldPassword(PasswordDto passwordDto);
}
