package com.nelani.blog_land_backend.Util;

import com.nelani.blog_land_backend.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class UserValidation {

    public static Optional<User> getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            return Optional.empty();
        }
        return Optional.of((User) auth.getPrincipal());
    }

}
