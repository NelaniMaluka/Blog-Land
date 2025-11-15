package com.nelani.blog_land_backend.model;

import com.nelani.blog_land_backend.security.ApplicationUserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        @Builder.Default
        @Column(nullable = false, unique = true, updatable = false)
        private String naniId = "nani_" + UUID.randomUUID().toString().substring(0, 8);

        private String username;

        @Builder.Default
        private boolean enabled = true;

        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        @Size(max = 50, message = "Title must not exceed 50 characters")
        @Column(length = 50)
        private String title;

        @Size(max = 500, message = "Summary must not exceed 500 characters")
        @Column(length = 500)
        private String summary;

        @Email(message = "Email should be valid")
        @NotBlank(message = "Email is required")
        @Column(unique = true, nullable = false)
        private String email;

        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name must not exceed 100 characters")
        @Column(nullable = false, length = 100)
        private String firstname;

        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name must not exceed 100 characters")
        @Column(nullable = false, length = 100)
        private String lastname;

        @Builder.Default
        @Enumerated(EnumType.STRING)
        private Provider provider = Provider.LOCAL;

        @Size(max = 500, message = "Profile icon URL must not exceed 500 characters")
        private String profileIconUrl;

        @Size(max = 255, message = "Location must not exceed 255 characters")
        private String location;

        @Enumerated(EnumType.STRING)
        private ExperienceLevel experience;

        @Builder.Default
        @Column(nullable = false, updatable = false)
        private LocalDateTime joinedAt = LocalDateTime.now();

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        @Builder.Default
        private ApplicationUserRole role = ApplicationUserRole.USER;

        @Override
        public String toString() {
                return "User{id=" + id + ", name='" + firstname + " " + lastname + "'}";
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
                return role.grantedAuthorities();
        }

        @Override
        public boolean isAccountNonExpired() {
                return true;
        }

        @Override
        public boolean isAccountNonLocked() {
                return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
                return true;
        }

        @Override
        public boolean isEnabled() {
                return enabled;
        }

        @PrePersist
        @PreUpdate
        public void syncUsernameWithEmail() {
                this.username = this.email;
        }
}
