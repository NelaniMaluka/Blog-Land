package com.nelani.blog_land_backend.config;

import com.nelani.blog_land_backend.Response.UserResponse;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private JwtUtil jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String firstName = oauthUser.getAttribute("given_name");
        String lastName = oauthUser.getAttribute("family_name");

        User user = userRepo.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFirstname(firstName);
            newUser.setLastname(lastName);
            newUser.setProvider("GOOGLE");
            return userRepo.save(newUser);
        });

        String token = jwtUtils.generateJwtToken(user);

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getProfileIconUrl(),
                user.getLocation(),
                token
        );

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Use Jackson ObjectMapper or similar to serialize userResponse to Json
        String json = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(userResponse);

        response.getWriter().write(json);
        response.getWriter().flush();
        response.getWriter().close();
    }
}

