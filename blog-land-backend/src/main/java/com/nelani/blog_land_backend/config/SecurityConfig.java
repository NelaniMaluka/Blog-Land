package com.nelani.blog_land_backend.config;

import com.nelani.blog_land_backend.Util.JwtUtil;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.service.CustomSuccessHandler;
import com.nelani.blog_land_backend.service.UnifiedAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomSuccessHandler customSuccessHandler;

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }

    @Bean
    public UnifiedAuthenticationFilter unifiedAuthenticationFilter(JwtUtil jwtUtil,
                                                                   UserRepository userRepository) {
        return new UnifiedAuthenticationFilter(jwtUtil, userRepository);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   UnifiedAuthenticationFilter unifiedAuthenticationFilter) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/oauth2/**",
                                "/api/v1/auth/oauth2/callback",
                                "/error",
                                "/h2-console/**",
                                "/api/contact-us/**",
                                "/api/newsletter/**",
                                "/api/category/**",
                                "/api/post/get/**",
                                "/api/comments/get/**",
                                "/api/post/api/search/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2.successHandler(customSuccessHandler))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                        )
                )
                .addFilterBefore(unifiedAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
