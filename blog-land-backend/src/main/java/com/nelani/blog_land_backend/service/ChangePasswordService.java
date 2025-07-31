package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.dto.PasswordDto;

public interface ChangePasswordService {
    void changePasswordWithOldPassword(PasswordDto passwordDto);
}
