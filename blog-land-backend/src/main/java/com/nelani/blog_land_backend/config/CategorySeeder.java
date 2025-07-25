package com.nelani.blog_land_backend.config;

import com.nelani.blog_land_backend.model.Category;
import com.nelani.blog_land_backend.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CategorySeeder {

    @Bean
    CommandLineRunner loadInitialCategories(CategoryRepository categoryRepository) {
        return args -> {
            List<String> categoryNames = List.of(
                    "Technology", "Health", "Finance", "Travel", "Lifestyle", "Education", "Food", "Science", "Sports",
                    "Culture", "Fashion", "Entertainment", "Politics", "Environment", "Parenting", "Career",
                    "Personal Development", "Business", "Photography", "DIY", "Other"
            );

            categoryNames.forEach(name -> {
                if (!categoryRepository.existsByName(name)) {
                    categoryRepository.save(Category.builder().name(name).build());
                }
            });
        };
    }
}

