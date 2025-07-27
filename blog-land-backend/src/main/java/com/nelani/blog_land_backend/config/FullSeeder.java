package com.nelani.blog_land_backend.config;

import com.nelani.blog_land_backend.repository.CategoryRepository;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
@Profile("!test")
public class FullSeeder {

    @Bean
    CommandLineRunner masterSeeder(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            CategoryRepository categoryRepository,
            PostRepository postRepository,
            RestTemplate restTemplate) {

        return args -> {
            new UserSeeder().seed(userRepository, passwordEncoder);
            new CategorySeeder().seed(categoryRepository);
            new TechCrunchSeeder().seed(restTemplate, postRepository, userRepository, categoryRepository );
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

