package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.dto.EmailDto;
import com.nelani.blog_land_backend.dto.ForgotPasswordDto;

public interface ForgotPasswordService {
    void requestPasswordReset(EmailDto emailDto);

    void changePassword(ForgotPasswordDto forgotPasswordDto);
}
