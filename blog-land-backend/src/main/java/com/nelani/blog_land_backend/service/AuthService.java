package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.dto.LoginUserDto;
import com.nelani.blog_land_backend.dto.RegisterUserDto;
import com.nelani.blog_land_backend.response.LoginResponse;

public interface AuthService {
    LoginResponse loginUser(LoginUserDto user);

    LoginResponse registerUser(RegisterUserDto user);

}
