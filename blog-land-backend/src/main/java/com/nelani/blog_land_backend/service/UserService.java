package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.dto.UpdateUserDto;
import com.nelani.blog_land_backend.response.LoginResponse;
import com.nelani.blog_land_backend.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    void removeUserProfileImage();

    void saveUserProfileImage(MultipartFile file);

    UserResponse getUserDetails();

    UserResponse getPublicUserDetails(String userNanoId);

    LoginResponse updateUserDetails(UpdateUserDto user);

    void deleteUserDetails();

}
