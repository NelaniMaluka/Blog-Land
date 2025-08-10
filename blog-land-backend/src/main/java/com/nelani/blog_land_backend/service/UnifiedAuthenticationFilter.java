package com.nelani.blog_land_backend.service;

import com.nelani.blog_land_backend.Util.JwtUtil;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

public class UnifiedAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate; // Add Redis

    public UnifiedAuthenticationFilter(JwtUtil jwtUtil,
                                       UserRepository userRepository,
                                       RedisTemplate<String, String> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            // 1. Check if token is blacklisted in Redis
            if (Boolean.TRUE.equals(redisTemplate.hasKey(token))) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has been logged out.");
                return;
            }

            // 2. Validate JWT
            if (!jwtUtil.validateJwtToken(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token.");
                return;
            }

            // 3. Get user from token
            String email = jwtUtil.getEmailFromJwtToken(token);
            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found for token.");
                return;
            }

            User user = userOpt.get();

            // 4. Inject authentication into context if not already present
            Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
            boolean shouldInject = (existingAuth == null)
                    || !(existingAuth instanceof UsernamePasswordAuthenticationToken);

            if (shouldInject) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
