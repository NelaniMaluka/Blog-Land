package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.response.UserResponse;

public interface UserService {
    UserResponse getUserDetails();

    String updateUserDetails(User user);

    void deleteUserDetails();
}
