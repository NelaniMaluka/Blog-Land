package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.response.ErrorResponse;
import com.nelani.blog_land_backend.response.UserResponse;
import com.nelani.blog_land_backend.Util.FormValidation;
import com.nelani.blog_land_backend.config.JwtUtil;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public ResponseEntity<?> updateUserDetails(User updateUser){
        try{

            // trim and validate fields
            String firstname = FormValidation.trimAndValidate(updateUser.getFirstname(), "Firstname");
            String lastname = FormValidation.trimAndValidate(updateUser.getLastname(), "Lastname");
            String email = FormValidation.trimAndValidate(updateUser.getEmail(), "Email");
            String provider = FormValidation.trimAndValidate(updateUser.getProvider(), " Provider");
            String profileIconUrl = updateUser.getProfileIconUrl();
            String location = updateUser.getLocation();

            // Validate email format
            if (!FormValidation.isValidEmail(email)){
                return ResponseEntity.badRequest().body(new ErrorResponse("Invalid email format",
                        "The provided email address is not valid. Please provide a valid email address."));
            }

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth == null || !(auth.getPrincipal() instanceof User)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("error",
                                "No authenticated user found"));

            }

            User user = (User) auth.getPrincipal();

            // Restricts email change to local users
            if (!user.getProvider().equals("LOCAL") && !user.getEmail().equals(email)){
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Authentication Type Error",
                        "OAuth user's can not change their email."));
            }

            // Validates user provider
            if (!user.getProvider().equals(provider)){
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Authentication Type Error",
                                "The provided authentication type is not valid."));
            }

            user.setFirstname(firstname);
            user.setLastname(lastname);
            user.setEmail(email);
            user.setProfileIconUrl(profileIconUrl);
            user.setLocation(location);

            userRepository.save(user);

            // Generate new token with updated email
            String newToken = jwtUtil.generateJwtToken(user);

            UserResponse responsePayload = new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getFirstname(),
                    user.getLastname(),
                    user.getProvider(),
                    user.getProfileIconUrl(),
                    user.getLocation(),
                    newToken
            );

            return ResponseEntity.ok(responsePayload);
        }  catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal Server Error",
                            "An unexpected error occurred while saving your message. Please try again later."));
        }
    }

}