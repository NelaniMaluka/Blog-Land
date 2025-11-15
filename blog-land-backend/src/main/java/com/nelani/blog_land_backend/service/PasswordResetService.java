package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.dto.ForgotPasswordDto;
import com.nelani.blog_land_backend.dto.PasswordDto;

public interface PasswordResetService {

    void requestPasswordReset(String email);

    void changePassword(ForgotPasswordDto passwordDto, String token);

    void changePasswordWithOldPassword(PasswordDto passwordDto);
}
