package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.model.User;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<?> getUserDetails();

    ResponseEntity<?> updateUserDetails(User user);

    ResponseEntity<?> deleteUserDetails(Long id);
}
