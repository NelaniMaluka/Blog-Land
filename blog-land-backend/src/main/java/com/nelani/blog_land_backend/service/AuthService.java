package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.dto.UserDto;

import java.util.Map;

public interface AuthService {
    String loginUser(Map<String, String> payload);

    String registerUser(UserDto user);

}
