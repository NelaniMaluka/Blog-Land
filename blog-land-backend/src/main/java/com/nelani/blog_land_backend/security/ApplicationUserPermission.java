package com.nelani.blog_land_backend.security;

import lombok.Getter;

@Getter
public enum ApplicationUserPermission {

    // User permissions
    USER_READ("user:read"),
    USER_WRITE("user:write"),
    USER_DELETE("user:delete"),
    USER_MANAGE_ROLES("user:manage_roles"),

    // Post permissions
    POST_READ("post:read"),
    POST_WRITE("post:write"),
    POST_DELETE("post:delete"),
    POST_PUBLISH("post:publish"),
    POST_IMAGE_UPLOAD("post:image_upload"),

    // Comment permissions
    COMMENT_READ("comment:read"),
    COMMENT_WRITE("comment:write"),
    COMMENT_DELETE("comment:delete"),

    // Like permissions
    LIKE_READ("like:read"),
    LIKE_WRITE("like:write"),
    LIKE_DELETE("like:delete");

    private final String permission;

    ApplicationUserPermission(String permission) {
        this.permission = permission;
    }
}
