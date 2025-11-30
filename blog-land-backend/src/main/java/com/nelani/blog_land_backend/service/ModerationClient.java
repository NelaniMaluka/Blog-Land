package com.nelani.blog_land_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nelani.blog_land_backend.exception.ValidationExceptionWithData;
import jakarta.validation.ValidationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Service
public class ModerationClient {

    @Value("${HUGGINGFACE_API_KEY}")
    private String apiKey;

    private static final String MODEL = "unitary/toxic-bert";

    private static final String ENDPOINT =
            "https://router.huggingface.co/hf-inference/models/" + MODEL;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // --------------------------------------------------
    // PUBLIC VALIDATION
    // --------------------------------------------------
    @Retryable(
            retryFor = {  HttpTimeoutException.class, ConnectException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void validateContent(String content) {
        Map<String, Double> flagged = getFlaggedLabels(content);
        if (!flagged.isEmpty()) {
            throw new ValidationExceptionWithData("Content flagged as inappropriate", flagged);
        }
    }

    // --------------------------------------------------
    // CORE LOGIC
    // --------------------------------------------------
    private Map<String, Double> getFlaggedLabels(String content) {

        Map<String, Double> flagged = new HashMap<>();

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("inputs", content);

            String jsonBody = mapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .header("X-Wait-For-Model", "true")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("Moderation API response status: {}", response.statusCode());
            log.debug("Moderation API response body: {}", response.body());

            if (response.statusCode() != 200) {
                throw new ValidationException("Content moderation failed");
            }

            JsonNode root = mapper.readTree(response.body());

            // HF router errors
            if (root.has("error")) {
                String errorMessage = root.get("error").asText();
                log.error("Moderation API returned an error: {}", errorMessage);
                throw new ValidationException("Content moderation failed");
            }

            // Case 1: Nested array → [[{label, score}, ...]]
            if (root.isArray() && root.get(0).isArray()) {
                for (JsonNode item : root.get(0)) {
                    parseLabel(flagged, item);
                }
            }
            // Case 2: Single array → [{label, score}, ...]
            else if (root.isArray()) {
                for (JsonNode item : root) {
                    parseLabel(flagged, item);
                }
            } else {
                log.error("Unexpected moderation response format: {}", root.toString());
                throw new ValidationException("Content moderation failed");
            }

        } catch (Exception e) {
            log.error("Moderation service exception: ", e);
            throw new ValidationException("Content moderation failed");
        }

        return flagged;
    }

    // --------------------------------------------------
    // PARSE HELPER
    // --------------------------------------------------
    private void parseLabel(Map<String, Double> flagged, JsonNode item) {
        if (!item.has("label") || !item.has("score")) return;

        String label = item.get("label").asText();
        double score = item.get("score").asDouble();

        double threshold = getThreshold(label);

        if (score >= threshold) {
            flagged.put(label, score);
        }
    }

    // --------------------------------------------------
    // CUSTOM THRESHOLDS
    // --------------------------------------------------
    private double getThreshold(String label) {
        return switch (label.toLowerCase()) {
            case "toxic" -> 0.6;
            case "severe_toxic" -> 0.5;
            case "obscene" -> 0.6;
            case "threat" -> 0.5;
            case "insult" -> 0.6;
            case "identity_hate" -> 0.5;
            default -> 0.8;
        };
    }

    @Recover
    public void recoverValidation(ValidationExceptionWithData e, String content) {
        Map<String, Double> flagged = e.getFlagged();
        flagged.forEach((label, score) ->
                log.info("Final flagged label: {}, score: {}", label, score)
        );
        log.error("Content validation failed after retries", e);
    }

}
