package com.nelani.blog_land_backend.service;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class ModerationClient {

    @Value("${HUGGINGFACE_API_KEY}")
    private String apiKey;

    private static final String ENDPOINT = "https://api-inference.huggingface.co/models/unitary/toxic-bert";

    public void validateContent(String content) {
        Map<String, Double> flagged = getFlaggedLabels(content);
        if (!flagged.isEmpty()) {
            StringBuilder sb = new StringBuilder("Content flagged for moderation:\n");
            flagged.forEach((label, score) -> sb.append("- ").append(label).append(": ").append(String.format("%.2f", score)).append("\n"));
            throw new ValidationException(sb.toString());
        }
    }

    public Map<String, Double> getFlaggedLabels(String content) {
        Map<String, Double> flaggedLabels = new HashMap<>();
        try {
            String escapedContent = content.replace("\"", "\\\"");
            String json = "{ \"inputs\": \"" + escapedContent + "\" }";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            JSONArray outerArray = new JSONArray(response.body());
            JSONArray scoresArray = outerArray.getJSONArray(0);

            for (int i = 0; i < scoresArray.length(); i++) {
                JSONObject labelScore = scoresArray.getJSONObject(i);
                String label = labelScore.getString("label");
                double score = labelScore.getDouble("score");

                double threshold = getThresholdForLabel(label);
                if (score > threshold) {
                    flaggedLabels.put(label, score);
                }
            }

        } catch (Exception e) {
            throw new ValidationException("Moderation service error.");
        }

        return flaggedLabels;
    }

    private double getThresholdForLabel(String label) {
        return switch (label.toLowerCase()) {
            case "toxicity" -> 0.75;
            case "hate" -> 0.80;
            case "insult" -> 0.85;
            default -> 0.90;
        };
    }
}