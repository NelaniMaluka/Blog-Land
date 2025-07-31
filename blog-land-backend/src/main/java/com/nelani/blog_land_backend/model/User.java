package com.nelani.blog_land_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String password;

    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    private Provider provider; // GOOGLE or LOCAL

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private String profileIconUrl;

    private String location;

    @Enumerated(EnumType.STRING)
    private ExperienceLevel experience;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "author_socials", joinColumns = @JoinColumn(name = "author_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
            "author_id", "platform" }))
    @Column(name = "url")
    @MapKeyColumn(name = "platform")
    private Map<String, String> socials = new HashMap<>();

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + firstname + " " + lastname + "'}";
    }
}
