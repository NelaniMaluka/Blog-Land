package com.nelani.blog_land_backend.config;

import com.nelani.blog_land_backend.model.Role;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public class UserSeeder {

    public void seed(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        List<String> firstNames = List.of("Ava", "Liam", "Zoe", "Ethan", "Maya", "Noah", "Ivy", "Leo", "Aria", "Kai",
                "Nia", "Jude", "Isla", "Owen", "Luna", "Micah", "Freya", "Ezra", "Skye", "Milo");
        List<String> lastNames = List.of("Smith", "Brown", "Taylor", "Morris", "Jones", "Nguyen", "Khan", "Garcia", "Ali", "Williams",
                "Adams", "Chen", "Patel", "Singh", "Carter", "Baker", "Thomas", "Lee", "Martin", "Hill");
        List<String> locations = List.of("Cape Town", "Pretoria", "Johannesburg", "Durban", "Bloemfontein",
                "Port Elizabeth", "East London", "Nelspruit", "Polokwane", "Kimberley");

        if (userRepository.count() > 0) {
            System.out.println("⚠️ Skipping seeding — posts already exist.");
            return;
        }

        for (int i = 0; i < 20; i++) {
            String firstname = firstNames.get(i);
            String lastname = lastNames.get(i);
            String email = firstname.toLowerCase() + "." + lastname.toLowerCase() + "@blogland.dev";
            String location = locations.get(i % locations.size());

            if (userRepository.findByEmail(email).isEmpty()) {
                User user = User.builder()
                        .firstname(firstname)
                        .lastname(lastname)
                        .email(email)
                        .password(passwordEncoder.encode("password123"))
                        .location(location)
                        .provider("LOCAL")
                        .role(Role.USER)
                        .profileIconUrl(null)
                        .build();

                userRepository.save(user);
            }
        }

        System.out.println("✅ User seeding complete.");
    }
}
