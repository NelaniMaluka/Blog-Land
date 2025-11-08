package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.PasswordReset;
import com.nelani.blog_land_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, UUID> {
   Optional<PasswordReset> findByToken(String token);

   Optional<PasswordReset> findByUserAndUsedFalseAndExpiryDateAfter(User user, LocalDateTime now);

   @Modifying
   @Transactional
   @Query("DELETE FROM PasswordReset pr WHERE pr.user = :user")
   int deleteByUser(@Param("user") User user);
}
