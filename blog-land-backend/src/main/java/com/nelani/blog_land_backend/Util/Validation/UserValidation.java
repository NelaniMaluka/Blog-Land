package com.nelani.blog_land_backend.util.validation;

import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class UserValidation {
    private final UserRepository userRepository;

    public UserValidation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetails principal)) {
            log.warn("Attempt to access authenticated user but none found in security context.");
            throw new BadCredentialsException("No authenticated user found.");
        }

        // Assuming username == email in your system
        return userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Authenticated user not found in database."));
    }

}
