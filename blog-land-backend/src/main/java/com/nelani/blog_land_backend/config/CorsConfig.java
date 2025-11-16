package com.nelani.blog_land_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration corsConfiguration = new CorsConfiguration();

                // Allowed origins: Render app + local dev
                corsConfiguration.setAllowedOrigins(List.of(
                                "http://localhost:5173",
                                "https://blog-land.web.app",
                                "https://blog-land.firebaseapp.com"));

                // Allowed HTTP methods
                corsConfiguration.setAllowedMethods(List.of(
                                "GET",
                                "POST",
                                "PUT",
                                "DELETE",
                                "OPTIONS"));

                // Allowed headers
                corsConfiguration.setAllowedHeaders(List.of(
                                "Authorization",
                                "Content-Type"));

                // Allow credentials if needed (cookies / JWTs)
                corsConfiguration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", corsConfiguration);
                return source;
        }

        // Static resource handler for profile icons
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/userProfileIcons/**")
                                .addResourceLocations("file:userProfileIcons/");
        }

}
