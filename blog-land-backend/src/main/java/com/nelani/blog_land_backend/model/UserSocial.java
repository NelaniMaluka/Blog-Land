package com.nelani.blog_land_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "user_socials", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "platform" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSocial {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "platform", length = 50, nullable = false)
    @Size(max = 50, message = "Platform name must not exceed 50 characters")
    private String platform;

    @Column(name = "url", length = 500, nullable = false)
    @Size(max = 500, message = "URL must not exceed 500 characters")
    private String url;
}
