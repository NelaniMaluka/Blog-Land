package com.nelani.blog_land_backend.security;

import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Log4j2
@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepo;
    private final JwtService jwtService;

    public CustomSuccessHandler(UserRepository userRepo, JwtService jwtService) {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        try {
            log.info("OAuth2 authentication successful");

            DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
            String email = oauthUser.getAttribute("email");
            String firstName = oauthUser.getAttribute("given_name");
            String lastName = oauthUser.getAttribute("family_name");

            log.debug("Authenticated user: email={}, firstName={}, lastName={}", email, firstName, lastName);

            // Fetch or create user
            User user = userRepo.findByEmail(email).orElseGet(() -> {
                log.info("Registering new OAuth2 user: {}", email);
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setFirstname(firstName);
                newUser.setLastname(lastName);
                newUser.setProvider(Provider.GOOGLE);
                return userRepo.save(newUser);
            });

            // Generate JWT token
            String token = jwtService.generateToken(user);
            log.debug("JWT generated for userId={}", user.getId());

            // Redirect to frontend with token
            String redirectUrl = "https://blog-land.web.app?token=" + token;
            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            log.error("OAuth2 authentication handling failed", e);
            throw e;
        }
    }
}
