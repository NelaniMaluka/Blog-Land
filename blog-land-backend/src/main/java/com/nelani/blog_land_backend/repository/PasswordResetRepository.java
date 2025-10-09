package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.PasswordReset;
import com.nelani.blog_land_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
   Optional<PasswordReset> findByToken(String token);
   Optional<PasswordReset> findByUserAndUsedFalseAndExpiryDateAfter(User user, LocalDateTime now);
}
