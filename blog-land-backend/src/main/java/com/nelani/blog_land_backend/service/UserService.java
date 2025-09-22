package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.dto.UserDto;
import com.nelani.blog_land_backend.response.UserResponse;
import com.nelani.blog_land_backend.response.UserSummaryAnalyticsResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    String removeUserProfileImage();

    String saveUserProfileImage(MultipartFile file);

    UserResponse getUserDetails();

    UserResponse getPublicUserDetails(String userNanoId);

    String updateUserDetails(UserDto user);

    void deleteUserDetails();

    UserSummaryAnalyticsResponse getUserSummaryAnalytics();
}
