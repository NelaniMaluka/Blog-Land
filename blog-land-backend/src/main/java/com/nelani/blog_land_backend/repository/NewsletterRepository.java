package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.Newsletter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NewsletterRepository extends JpaRepository<Newsletter, UUID> {
    Optional<Newsletter> findByEmail(String email);
}
