package com.nelani.blog_land_backend.util.validation;

import com.nelani.blog_land_backend.model.Comment;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.service.ModerationClient;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ModerationValidator {

    private final ModerationClient moderationClient;

    public ModerationValidator(ModerationClient moderationClient) {
        this.moderationClient = moderationClient;
    }

    public void commentModeration(Comment comment) {
        moderationClient.validateContent(comment.getContent());
    }

    public void postModeration(Post post) {
        StringBuilder combined = new StringBuilder();

        if (isValid(post.getTitle()))
            combined.append(post.getTitle()).append("\n");
        if (isValid(post.getContent()))
            combined.append(post.getContent()).append("\n");
        if (isValid(post.getSummary()))
            combined.append(post.getSummary()).append("\n");

        if (isValid(post.getReferences())) {
            String[] urls = post.getReferences().split("/\\\\\\*\\\\"); // existing split logic
            for (String url : urls) {
                url = url.trim();
                if (url.isEmpty())
                    continue;
                combined.append(url).append("\n");
                combined.append(fetchPageTitleOrSnippet(url)).append("\n");
            }
        }

        moderationClient.validateContent(combined.toString());
    }

    public void userModeration(User user, Map<String, String> socials) {
        StringBuilder combined = new StringBuilder();

        // Append user fields
        if (isValid(user.getFirstname()))
            combined.append(user.getFirstname()).append("\n");
        if (isValid(user.getLastname()))
            combined.append(user.getLastname()).append("\n");
        if (isValid(user.getTitle()))
            combined.append(user.getTitle()).append("\n");
        if (isValid(user.getSummary()))
            combined.append(user.getSummary()).append("\n");
        if (isValid(user.getLocation()))
            combined.append(user.getLocation()).append("\n");

        // Append socials
        if (socials != null) {
            for (Map.Entry<String, String> entry : socials.entrySet()) {
                String url = entry.getValue();
                if (isValid(url))
                    combined.append(url).append("\n");
                String snippet = fetchPageTitleOrSnippet(url);
                if (isValid(snippet))
                    combined.append(snippet).append("\n");
            }
        }

        moderationClient.validateContent(combined.toString());
    }

    private String fetchPageTitleOrSnippet(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String html = response.body();

            String title = extractTagContent(html, "title");
            String description = extractMetaDescription(html);

            return (title + " " + description).trim();
        } catch (Exception e) {
            return url;
        }
    }

    private String extractTagContent(String html, String tag) {
        int start = html.indexOf("<" + tag + ">");
        int end = html.indexOf("</" + tag + ">");
        return (start != -1 && end != -1 && end > start)
                ? html.substring(start + tag.length() + 2, end).trim()
                : "";
    }

    private String extractMetaDescription(String html) {
        Pattern pattern = Pattern.compile("<meta\\s+name=[\"']description[\"']\\s+content=[\"'](.*?)[\"']",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        return matcher.find() ? matcher.group(1).trim() : "";
    }

    private boolean isValid(String value) {
        return value != null && !value.isBlank();
    }
}
