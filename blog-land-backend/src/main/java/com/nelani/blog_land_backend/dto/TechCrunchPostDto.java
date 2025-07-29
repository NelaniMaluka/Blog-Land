package com.nelani.blog_land_backend.dto;

import lombok.Data;

@Data
public class TechCrunchPostDto {
    private Long id;
    private Title title;
    private Content content;
    private Excerpt excerpt;
    private String date;
    private Embedded _embedded;

    @Data public static class Title { private String rendered; }
    @Data public static class Content { private String rendered; }
    @Data public static class Excerpt { private String rendered; }

    @Data public static class Embedded {
        private Author[] author;
    }

    @Data public static class Author {
        private String name;
    }
}

