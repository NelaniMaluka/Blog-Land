package com.nelani.blog_land_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TechCrunchPostDto {
    private Long id;
    private Title title;
    private Content content;
    private Excerpt excerpt;
    private String date;

    @JsonProperty("_embedded")
    private Embedded embedded;  // note camelCase

    @Data public static class Title { private String rendered; }
    @Data public static class Content { private String rendered; }
    @Data public static class Excerpt { private String rendered; }

    @Data
    public static class Embedded {
        private Author[] author;

        @JsonProperty("wp:featuredmedia")
        private FeaturedMedia[] featuredmedia; // <-- add this
    }

    @Data public static class Author { private String name; }

    @Data
    public static class FeaturedMedia {
        @JsonProperty("source_url")
        private String sourceUrl; // <-- this is the image URL
    }
}
