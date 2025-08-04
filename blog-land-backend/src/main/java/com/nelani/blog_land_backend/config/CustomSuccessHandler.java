package com.nelani.blog_land_backend.config;

import com.nelani.blog_land_backend.Util.JwtUtil;
import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Lazy;


import java.io.IOException;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private UserRepository userRepo;
    private final JwtUtil jwtUtil;

    @Autowired
    public CustomSuccessHandler(UserRepository userRepo, @Lazy JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }


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
            newUser.setProvider(Provider.GOOGLE);
            return userRepo.save(newUser);
        });

        String token = jwtUtil.generateJwtToken(user);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Use Jackson ObjectMapper or similar to serialize userResponse to Json
        String json = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(token);

        response.getWriter().write(json);
        response.getWriter().flush();
        response.getWriter().close();
    }
}
