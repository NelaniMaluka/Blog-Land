package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.dto.LoginUserDto;
import com.nelani.blog_land_backend.dto.RegisterUserDto;

public interface AuthService {
    String loginUser(LoginUserDto user);
    String registerUser(RegisterUserDto user);

}
