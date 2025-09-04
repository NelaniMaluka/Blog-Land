package com.nelani.blog_land_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "users", schema = "auth")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Size(min = 6, message = "Password must be at least 6 characters")
        @Column(nullable = true)
        private String password;

        @Size(max = 50, message = "Title must not exceed 50 characters")
        @Column(nullable = true, length = 50)
        private String title;

        @Size(max = 500, message = "Summary must not exceed 500 characters")
        @Column(nullable = true, length = 500)
        private String summary;

        @Email(message = "Email should be valid")
        @NotBlank(message = "Email is required")
        @Column(unique = true, nullable = false, length = 255)
        private String email;

        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name must not exceed 100 characters")
        @Column(nullable = false, length = 100)
        private String firstname;

        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name must not exceed 100 characters")
        @Column(nullable = false, length = 100)
        private String lastname;

        @Enumerated(EnumType.STRING)
        private Provider provider; // GOOGLE or LOCAL

        @Builder.Default
        @Enumerated(EnumType.STRING)
        private Role role = Role.USER;

        @Size(max = 500, message = "Profile icon URL must not exceed 500 characters")
        private String profileIconUrl;

        @Size(max = 255, message = "Location must not exceed 255 characters")
        private String location;

        @Enumerated(EnumType.STRING)
        private ExperienceLevel experience;

        @Builder.Default
        @Column(nullable = false, updatable = false)
        private LocalDateTime joinedAt = LocalDateTime.now();

        @Builder.Default
        @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
        private List<Post> posts = new ArrayList<>();

        @Builder.Default
        @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
        private List<Comment> comments = new ArrayList<>();

        @Builder.Default
        @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
        private List<Like> likes = new ArrayList<>();

        @Builder.Default
        @BatchSize(size = 10)
        @ElementCollection(fetch = FetchType.LAZY)
        @CollectionTable(name = "author_socials", joinColumns = @JoinColumn(name = "author_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
                        "author_id", "platform" }))
        @Column(name = "url")
        @MapKeyColumn(name = "platform")
        private Map<@NotBlank(message = "Platform name cannot be blank") @Size(max = 50, message = "Platform name must not exceed 50 characters") String,

                        @NotBlank(message = "URL cannot be blank") @Size(max = 500, message = "URL must not exceed 500 characters") String> socials = new HashMap<>();

        @Override
        public String toString() {
                return "User{id=" + id + ", name='" + firstname + " " + lastname + "'}";
        }
}
