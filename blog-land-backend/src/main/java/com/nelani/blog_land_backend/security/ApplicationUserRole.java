package com.nelani.blog_land_backend.security;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.nelani.blog_land_backend.security.ApplicationUserPermission.*;

@Getter
public enum ApplicationUserRole {

        USER(Set.of(
                        USER_READ,
                        USER_WRITE,
                        USER_DELETE,

                        POST_READ,
                        POST_WRITE,
                        POST_DELETE,
                        COMMENT_READ,
                        COMMENT_WRITE,
                        COMMENT_DELETE,
                        LIKE_READ,
                        LIKE_WRITE,
                        LIKE_DELETE)),

        MODERATOR(Set.of(
                        USER_READ,
                        POST_READ,
                        POST_DELETE,
                        POST_PUBLISH,
                        COMMENT_READ,
                        COMMENT_DELETE,
                        LIKE_READ,
                        LIKE_DELETE)),

        ADMIN(Set.of(
                        USER_READ,
                        USER_WRITE,
                        USER_DELETE,
                        USER_MANAGE_ROLES,
                        POST_READ,
                        POST_WRITE,
                        POST_DELETE,
                        POST_PUBLISH,
                        POST_IMAGE_UPLOAD,
                        COMMENT_READ,
                        COMMENT_WRITE,
                        COMMENT_DELETE,
                        LIKE_READ,
                        LIKE_WRITE,
                        LIKE_DELETE));

        private final Set<ApplicationUserPermission> permissions;

        ApplicationUserRole(Set<ApplicationUserPermission> permissions) {
                this.permissions = permissions;
        }

        public Set<SimpleGrantedAuthority> grantedAuthorities() {
                Set<SimpleGrantedAuthority> authorities = getPermissions().stream()
                                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                                .collect(Collectors.toSet());
                authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
                return authorities;
        }
}
