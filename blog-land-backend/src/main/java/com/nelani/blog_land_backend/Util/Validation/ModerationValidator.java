package com.nelani.blog_land_backend.Util.Validation;

import com.nelani.blog_land_backend.model.Comment;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.service.ModerationClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModerationValidator {

    public static void commentModeration(Comment comment, ModerationClient moderationClient){
        moderationClient.validateContent(comment.getContent());
    }

    public static void postModeration(Post post, ModerationClient moderationClient){
        moderationClient.validateContent(post.getTitle());
        moderationClient.validateContent(post.getContent());
        moderationClient.validateContent(post.getSummary());
        if(isValid(post.getReferences())) moderateReferences(post.getReferences(), moderationClient);
    }

    public static void userModeration(User user, ModerationClient moderationClient){
        moderationClient.validateContent(user.getFirstname());
        moderationClient.validateContent(user.getLastname());
        if(isValid(user.getLocation())) moderationClient.validateContent(user.getLocation());

        if (user.getSocials() != null) {
            for (Map.Entry<String, String> entry : user.getSocials().entrySet()) {
                String platform = entry.getKey();
                String url = entry.getValue();

                // Option 1: Moderate the raw URL string
                moderationClient.validateContent(url);

                // Option 2 (recommended): Fetch preview/title and moderate that
                String preview = fetchPageTitleOrSnippet(url);
                moderationClient.validateContent(preview);
            }
        }
    }

    public static String fetchPageTitleOrSnippet(String url) {
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
            return url; // fallback if fetch fails
        }
    }

    private static String extractTagContent(String html, String tag) {
        int start = html.indexOf("<" + tag + ">");
        int end = html.indexOf("</" + tag + ">");
        if (start != -1 && end != -1 && end > start) {
            return html.substring(start + tag.length() + 2, end).trim();
        }
        return "";
    }

    private static String extractMetaDescription(String html) {
        Pattern pattern = Pattern.compile("<meta\\s+name=[\"']description[\"']\\s+content=[\"'](.*?)[\"']", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }

    private static void moderateReferences(String references, ModerationClient moderationClient) {
        if (references == null || references.isBlank()) return;

        String[] urls = references.split("/\\\\\\*\\\\"); // Escaped regex for /*\

        for (String url : urls) {
            url = url.trim();
            if (url.isEmpty()) continue;

            // Moderate raw URL
            moderationClient.validateContent(url);

            // Fetch and moderate page title/snippet
            String preview = fetchPageTitleOrSnippet(url);
            moderationClient.validateContent(preview);
        }
    }

    private static boolean isValid(String value) {
        return value != null && !value.isBlank();
    }
}
