package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.PasswordResetToken;
import com.nelani.blog_land_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
   Optional<PasswordResetToken> findByUserAndUsedFalseAndExpiryDateAfter(User user, LocalDateTime now);
   Optional<PasswordResetToken> findByToken(String token);
}
