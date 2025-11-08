package com.nelani.blog_land_backend.seeder;

import com.nelani.blog_land_backend.model.*;
import com.nelani.blog_land_backend.repository.UserRepository;
import com.nelani.blog_land_backend.repository.UserSocialRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class UserSeeder {

        private final UserRepository userRepository;
        private final UserSocialRepository userSocialRepository;
        private final PasswordEncoder passwordEncoder;

        public UserSeeder(UserRepository userRepository,
                        UserSocialRepository userSocialRepository,
                        PasswordEncoder passwordEncoder) {
                this.userRepository = userRepository;
                this.userSocialRepository = userSocialRepository;
                this.passwordEncoder = passwordEncoder;
        }

        public void seed() {
                if (userRepository.count() > 0) {
                        System.out.println("⚠️ Skipping seeding — users already exist.");
                        return;
                }

                List<String> firstNames = List.of("Ava", "Liam", "Zoe", "Ethan", "Maya", "Noah", "Ivy", "Leo", "Aria",
                                "Kai", "Nia", "Jude", "Isla", "Owen", "Luna", "Micah", "Freya", "Ezra", "Skye", "Milo");

                List<String> lastNames = List.of("Smith", "Brown", "Taylor", "Morris", "Jones", "Nguyen", "Khan",
                                "Garcia",
                                "Ali", "Williams", "Adams", "Chen", "Patel", "Singh", "Carter", "Baker", "Thomas",
                                "Lee", "Martin", "Hill");

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
                                "Nairobi, Kenya");

                List<ExperienceLevel> experienceLevels = List.of(
                                ExperienceLevel.NEW_BLOGGER,
                                ExperienceLevel.CASUAL_POSTER,
                                ExperienceLevel.COMMUNITY_WRITER,
                                ExperienceLevel.FREQUENT_CONTRIBUTOR,
                                ExperienceLevel.PRO_BLOGGER);

                List<String> summaries = List.of(
                                "Writes about coding, coffee, and late-night debugging sessions.",
                                "Sharing thoughts on tech, games, and random internet finds.",
                                "Exploring web development while documenting the journey.",
                                "Just a curious mind posting tutorials and opinions.",
                                "Into gadgets, apps, and digital minimalism.",
                                "Breaking down complex topics into simple blog posts.",
                                "Enjoys reviewing new tools and frameworks.",
                                "Documenting travel, tech, and personal projects.",
                                "Passionate about security, privacy, and open source.",
                                "Blogging for fun and learning along the way.");

                List<String> titles = List.of(
                                "Tech Enthusiast",
                                "Full-Stack Explorer",
                                "Gamer & Blogger",
                                "Student Developer",
                                "Creative Writer",
                                "Foodie Reviewer",
                                "Travel Storyteller",
                                "UI/UX Tinkerer",
                                "AI Curious",
                                "Cybersecurity Advocate");

                Random random = new Random();

                for (int i = 0; i < 20; i++) {
                        String firstname = firstNames.get(i);
                        String lastname = lastNames.get(i);
                        String email = firstname.toLowerCase() + "." + lastname.toLowerCase() + "@blogland.dev";

                        if (userRepository.findByEmail(email).isEmpty()) {
                                User user = User.builder()
                                                .firstname(firstname)
                                                .lastname(lastname)
                                                .email(email)
                                                .password(passwordEncoder.encode("password@123"))
                                                .location(locations.get(i % locations.size()))
                                                .provider(Provider.LOCAL)
                                                .profileIconUrl(null)
                                                .experience(experienceLevels.get(i % experienceLevels.size()))
                                                .summary(summaries.get(random.nextInt(summaries.size())))
                                                .title(titles.get(random.nextInt(titles.size())))
                                                .build();

                                userRepository.save(user);

                                // ✅ Create UserSocial entries
                                userSocialRepository.save(UserSocial.builder()
                                                .user(user)
                                                .platform("twitter")
                                                .url("https://twitter.com/" + firstname.toLowerCase() + "_blogger")
                                                .build());

                                userSocialRepository.save(UserSocial.builder()
                                                .user(user)
                                                .platform("github")
                                                .url("https://github.com/" + firstname.toLowerCase()
                                                                + lastname.toLowerCase())
                                                .build());

                                userSocialRepository.save(UserSocial.builder()
                                                .user(user)
                                                .platform("linkedin")
                                                .url("https://linkedin.com/in/" + firstname.toLowerCase()
                                                                + lastname.toLowerCase())
                                                .build());
                        }
                }

                System.out.println("✅ User + Social seeding complete.");
        }
}
