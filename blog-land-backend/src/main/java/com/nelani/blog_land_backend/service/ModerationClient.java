package com.nelani.blog_land_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class ModerationClient {

    @Value("${HUGGINGFACE_API_KEY}")
    private String apiKey;

    // Updated endpoint for Hugging Face router API
    private static final String ENDPOINT = "https://router.huggingface.co/hf-inference/text-moderation-latest";

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Validates content. Throws ValidationException if flagged.
     */
    public void validateContent(String content) {
        Map<String, Double> flagged = getFlaggedLabels(content);
        if (!flagged.isEmpty()) {
            throw new ValidationException("Content flagged for moderation: " + flagged.keySet());
        }
    }

    /**
     * Sends content to Hugging Face moderation API and returns flagged labels above
     * thresholds.
     */
    public Map<String, Double> getFlaggedLabels(String content) {
        Map<String, Double> flaggedLabels = new HashMap<>();

        try {
            // Build request body
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("inputs", content);
            String jsonBody = mapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("HF Response status: " + response.statusCode());
            System.out.println("HF Response body: " + response.body());

            if (response.statusCode() != 200) {
                throw new ValidationException("Moderation API error: " + response.body());
            }

            JsonNode root = mapper.readTree(response.body());
            if (root.has("results") && root.get("results").isArray() && root.get("results").size() > 0) {
                JsonNode categoryScoresNode = root.get("results").get(0).get("category_scores");
                if (categoryScoresNode != null) {
                    categoryScoresNode.fields().forEachRemaining(entry -> {
                        String label = entry.getKey();
                        double score = entry.getValue().asDouble();
                        double threshold = getThresholdForLabel(label);
                        if (score > threshold) {
                            flaggedLabels.put(label, score);
                        }
                    });
                }
            }

        } catch (Exception e) {
            throw new ValidationException("Moderation service error: " + e.getMessage(), e);
        }

        return flaggedLabels;
    }

    private double getThresholdForLabel(String label) {
        return switch (label.toLowerCase()) {
            case "sexual" -> 0.7;
            case "violence" -> 0.7;
            case "harassment" -> 0.75;
            default -> 0.85;
        };
    }

    /**
     * Returns the raw Hugging Face response as a string
     */
    public String getRawModerationResponse(String content) {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("inputs", content);
            String jsonBody = mapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();

        } catch (Exception e) {
            throw new ValidationException("Moderation service error: " + e.getMessage(), e);
        }
    }
}
