package com.nelani.blog_land_backend.seeder;

import com.nelani.blog_land_backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FullSeeder {

    @Bean
    CommandLineRunner masterSeeder(
            UserRepository userRepository,
            UserSocialRepository userSocialRepository,
            PasswordEncoder passwordEncoder,
            CategoryRepository categoryRepository,
            PostRepository postRepository,
            LikeRepository likeRepository,
            RestTemplate restTemplate) {

        return args -> {
            // 🧩 Step 1: Seed users first
            new UserSeeder(userRepository, userSocialRepository, passwordEncoder).seed();

            // 🧩 Step 2: Then seed categories
            new CategorySeeder().seed(categoryRepository);

            // 🧩 Step 3: Then seed TechCrunch posts (which depend on users & categories)
            new TechCrunchSeeder().seed(restTemplate, postRepository, userRepository, categoryRepository,
                    likeRepository);
        };
    }
}
