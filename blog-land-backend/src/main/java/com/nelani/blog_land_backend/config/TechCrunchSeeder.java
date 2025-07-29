package com.nelani.blog_land_backend.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.nelani.blog_land_backend.model.Category;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.CategoryRepository;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;
import java.util.*;

public class TechCrunchSeeder {

    public void seed(
            RestTemplate restTemplate,
            PostRepository postRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository) {

        int totalPostsToSeed = 20;
        int pageSize = 20;
        int pages = (int) Math.ceil((double) totalPostsToSeed / pageSize);
        int seededCount = 0;

        List<User> users = userRepository.findAll();
        List<Category> categories = categoryRepository.findAll();
        Random random = new Random();

        if (users.isEmpty() || categories.isEmpty()) {
            System.out.println("⚠️ Skipping post seeding — missing users or categories.");
            return;
        }

        if (postRepository.count() > 0) {
            System.out.println("⚠️ Skipping seeding — posts already exist.");
            return;
        }

        for (int page = 1; page <= pages && seededCount < totalPostsToSeed; page++) {
            String apiUrl = "https://techcrunch.com/wp-json/wp/v2/posts?per_page=" + pageSize + "&page=" + page
                    + "&_embed";

            try {
                ResponseEntity<JsonNode> response = restTemplate.getForEntity(apiUrl, JsonNode.class);

                if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                    System.out.println("⚠️ Failed to fetch posts from TechCrunch page " + page);
                    continue;
                }

                JsonNode posts = response.getBody();

                for (JsonNode postNode : posts) {
                    if (seededCount >= totalPostsToSeed)
                        break;

                    try {
                        JsonNode titleNode = postNode.path("title").path("rendered");
                        JsonNode contentNode = postNode.path("content").path("rendered");
                        JsonNode excerptNode = postNode.path("excerpt").path("rendered");
                        JsonNode linkNode = postNode.path("link");

                        if (titleNode.isMissingNode() || contentNode.isMissingNode() || excerptNode.isMissingNode()
                                || linkNode.isMissingNode()) {
                            continue;
                        }

                        String title = HtmlUtils.htmlUnescape(titleNode.asText());
                        String content = contentNode.asText();
                        String summary = excerptNode.asText();
                        String imageUrl = postNode.at("/_embedded/wp:featuredmedia/0/source_url").asText("");
                        int readTime = 3 + random.nextInt(5);
                        String referencesJson = linkNode.asText();

                        User user = users.get(random.nextInt(users.size()));
                        Category category = determineCategoryByScore(title, content, categories);

                        Post post = Post.builder()
                                .title(title)
                                .content(content)
                                .summary(summary)
                                .readTime(readTime)
                                .imgUrl(imageUrl)
                                .references(referencesJson)
                                .user(user)
                                .category(category)
                                .createdAt(LocalDateTime.now())
                                .build();

                        postRepository.save(post);
                        seededCount++;

                    } catch (Exception innerEx) {
                        System.out.println("❌ Skipping invalid post: " + innerEx.getMessage());
                    }
                }

            } catch (Exception e) {
                System.out.println("❌ Exception during TechCrunch seeding page " + page + ": " + e.getMessage());
            }
        }

        System.out.println("✅ Seeded " + seededCount + " TechCrunch posts.");
    }

    // 🧠 Score-Based Category Matching
    private Category determineCategoryByScore(String title, String content, List<Category> categories) {
        String combined = (title + " " + content).toLowerCase();

        Map<String, List<String>> keywordMap = Map.ofEntries(
                Map.entry("Technology", List.of("technology", "tech", "startup", "software", "hardware", "ai", "app")),
                Map.entry("Health", List.of("health", "medical", "wellness", "fitness", "mental")),
                Map.entry("Finance", List.of("finance", "investment", "economy", "crypto", "money")),
                Map.entry("Travel", List.of("travel", "trip", "flight", "tourism", "destination")),
                Map.entry("Lifestyle", List.of("lifestyle", "living", "trends", "routine")),
                Map.entry("Education", List.of("education", "school", "learning", "students", "teaching")),
                Map.entry("Food", List.of("food", "meal", "recipe", "cuisine", "restaurant")),
                Map.entry("Science", List.of("science", "research", "experiment", "space", "biology", "physics")),
                Map.entry("Sports", List.of("sports", "game", "match", "athlete", "score")),
                Map.entry("Culture", List.of("culture", "heritage", "tradition", "society")),
                Map.entry("Fashion", List.of("fashion", "style", "clothing", "trend", "runway")),
                Map.entry("Entertainment", List.of("entertainment", "movie", "tv", "show", "series", "celeb")),
                Map.entry("Politics", List.of("politics", "government", "election", "policy", "law")),
                Map.entry("Environment", List.of("environment", "climate", "sustainability", "earth", "green")),
                Map.entry("Parenting", List.of("parenting", "child", "baby", "mom", "dad", "family")),
                Map.entry("Career", List.of("career", "job", "work", "employee", "hiring", "recruiting")),
                Map.entry("Personal Development", List.of("growth", "development", "self", "mindset")),
                Map.entry("Business", List.of("business", "market", "enterprise", "startup", "company")),
                Map.entry("Photography", List.of("photography", "camera", "photo", "lens", "shoot")),
                Map.entry("DIY", List.of("diy", "craft", "handmade", "tutorial", "how to")));

        Map<Category, Integer> scoreMap = new HashMap<>();

        for (Category category : categories) {
            List<String> keywords = keywordMap.getOrDefault(category.getName(), List.of());
            int score = 0;
            for (String keyword : keywords) {
                if (combined.contains(keyword)) {
                    score++;
                }
            }
            scoreMap.put(category, score);
        }

        return scoreMap.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(getFallbackCategory(categories));
    }

    private Category getFallbackCategory(List<Category> categories) {
        return categories.stream()
                .filter(c -> c.getName().equalsIgnoreCase("Other"))
                .findFirst()
                .orElse(categories.get(0));
    }
}
