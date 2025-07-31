package com.nelani.blog_land_backend.config;

import com.nelani.blog_land_backend.model.ExperienceLevel;
import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.model.Role;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserSeeder {

    public void seed(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        List<String> firstNames = List.of("Ava", "Liam", "Zoe", "Ethan", "Maya", "Noah", "Ivy", "Leo", "Aria", "Kai",
                "Nia", "Jude", "Isla", "Owen", "Luna", "Micah", "Freya", "Ezra", "Skye", "Milo");
        List<String> lastNames = List.of("Smith", "Brown", "Taylor", "Morris", "Jones", "Nguyen", "Khan", "Garcia",
                "Ali", "Williams",
                "Adams", "Chen", "Patel", "Singh", "Carter", "Baker", "Thomas", "Lee", "Martin", "Hill");
        List<String> locations = List.of(
                "Cape Town, South Africa",
                "Pretoria, South Africa",
                "Johannesburg, South Africa",
                "Durban, South Africa",
                "Bloemfontein, South Africa",
                "Port Elizabeth, South Africa",
                "East London, South Africa",
                "Nelspruit, South Africa",
                "Polokwane, South Africa",
                "Kimberley, South Africa",
                "New York, United States",
                "Tokyo, Japan",
                "Paris, France",
                "London, United Kingdom",
                "Berlin, Germany",
                "São Paulo, Brazil",
                "Toronto, Canada",
                "Seoul, South Korea",
                "Sydney, Australia",
                "Nairobi, Kenya",
                "Bangkok, Thailand",
                "Moscow, Russia",
                "Dubai, United Arab Emirates",
                "Barcelona, Spain",
                "Istanbul, Turkey");
        List<ExperienceLevel> experienceLevels = List.of(
                ExperienceLevel.BEGINNER,
                ExperienceLevel.INTERMEDIATE,
                ExperienceLevel.ADVANCED,
                ExperienceLevel.EXPERT);

        if (userRepository.count() > 0) {
            System.out.println("⚠️ Skipping seeding — users already exist.");
            return;
        }

        for (int i = 0; i < 20; i++) {
            String firstname = firstNames.get(i);
            String lastname = lastNames.get(i);
            String email = firstname.toLowerCase() + "." + lastname.toLowerCase() + "@blogland.dev";
            String location = locations.get(i % locations.size());
            ExperienceLevel experience = experienceLevels.get(i % experienceLevels.size());

            Map<String, String> socials = new HashMap<>();
            socials.put("twitter", "@" + firstname.toLowerCase() + "_blogger");
            socials.put("github", "github.com/" + firstname.toLowerCase() + lastname.toLowerCase());
            socials.put("linkedin", "linkedin.com/in/" + firstname.toLowerCase() + lastname.toLowerCase());

            if (userRepository.findByEmail(email).isEmpty()) {
                User user = User.builder()
                        .firstname(firstname)
                        .lastname(lastname)
                        .email(email)
                        .password(passwordEncoder.encode("password@123"))
                        .location(location)
                        .provider(Provider.LOCAL)
                        .role(Role.USER)
                        .profileIconUrl(null)
                        .experience(experience) // Enum injected here
                        .socials(socials)
                        .build();

                userRepository.save(user);
            }
        }

        System.out.println("✅ User seeding complete.");
    }
}