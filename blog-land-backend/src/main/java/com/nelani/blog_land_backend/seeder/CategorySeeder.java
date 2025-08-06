package com.nelani.blog_land_backend.seeder;

import com.nelani.blog_land_backend.model.Category;
import com.nelani.blog_land_backend.repository.CategoryRepository;

import java.util.List;

public class CategorySeeder {

    public void seed(CategoryRepository categoryRepository) {
        List<String> categoryNames = List.of(
                "Technology", "Health", "Finance", "Travel", "Lifestyle", "Education", "Food", "Science", "Sports",
                "Culture", "Fashion", "Entertainment", "Politics", "Environment", "Parenting", "Career",
                "Personal Development", "Business", "Photography", "DIY", "Other");

        if (categoryRepository.count() > 0) {
            System.out.println("⚠️ Skipping seeding — posts already exist.");
            return;
        }

        categoryNames.forEach(name -> {
            if (!categoryRepository.existsByName(name)) {
                categoryRepository.save(Category.builder().name(name).build());
            }
        });

        System.out.println("✅ Categories seeded.");
    }
}
