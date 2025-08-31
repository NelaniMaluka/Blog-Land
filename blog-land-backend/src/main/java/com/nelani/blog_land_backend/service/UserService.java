package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    String removeUserProfileImage();

    String saveUserProfileImage(MultipartFile file);

    UserResponse getUserDetails();

    String updateUserDetails(User user);

    void deleteUserDetails();
}
