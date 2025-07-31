package com.nelani.blog_land_backend.service;

import java.util.Map;

public interface ForgotPasswordService {
    void requestPasswordReset(Map<String, String> payload);

    void changePassword(Map<String, String> payload);
}
