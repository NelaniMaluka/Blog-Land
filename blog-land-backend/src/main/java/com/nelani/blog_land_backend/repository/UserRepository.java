package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByNaniId(String naniId);
}
