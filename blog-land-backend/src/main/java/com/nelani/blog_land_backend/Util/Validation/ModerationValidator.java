package com.nelani.blog_land_backend.Util.Validation;

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
        moderationClient.validateContent(post.getTitle());
        moderationClient.validateContent(post.getContent());
        moderationClient.validateContent(post.getSummary());
        if (isValid(post.getReferences())) moderateReferences(post.getReferences());
    }

    public void userModeration(User user) {
        moderationClient.validateContent(user.getFirstname());
        moderationClient.validateContent(user.getLastname());
        if (isValid(user.getLocation())) moderationClient.validateContent(user.getLocation());

        if (user.getSocials() != null) {
            for (Map.Entry<String, String> entry : user.getSocials().entrySet()) {
                String url = entry.getValue();
                moderationClient.validateContent(url);
                moderationClient.validateContent(fetchPageTitleOrSnippet(url));
            }
        }
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

    private void moderateReferences(String references) {
        if (references == null || references.isBlank()) return;
        String[] urls = references.split("/\\\\\\*\\\\");
        for (String url : urls) {
            url = url.trim();
            if (url.isEmpty()) continue;
            moderationClient.validateContent(url);
            moderationClient.validateContent(fetchPageTitleOrSnippet(url));
        }
    }

    private boolean isValid(String value) {
        return value != null && !value.isBlank();
    }
}
