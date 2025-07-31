package com.nelani.blog_land_backend.config;

import com.nelani.blog_land_backend.repository.CategoryRepository;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ScheduledSeeder {

    private final TechCrunchSeeder techCrunchSeeder;
    private final RestTemplate restTemplate;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public ScheduledSeeder(
            TechCrunchSeeder techCrunchSeeder,
            RestTemplate restTemplate,
            PostRepository postRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository) {
        this.techCrunchSeeder = techCrunchSeeder;
        this.restTemplate = restTemplate;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Scheduled(cron = "0 0 8 1,15 * ?")
    public void seedLeastPopulatedCategories() {
        try {
            System.out.println("🕒 Running scheduled seeding for least-populated categories...");
            techCrunchSeeder.seedLeastPopulatedCategories(restTemplate, postRepository, userRepository, categoryRepository);
        } catch (Exception e) {
            System.err.println("❌ Scheduled seeding failed: " + e.getMessage());
        }
    }
}
