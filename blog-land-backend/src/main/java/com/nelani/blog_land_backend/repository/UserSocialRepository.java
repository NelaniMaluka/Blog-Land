package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.model.UserSocial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface UserSocialRepository extends JpaRepository<UserSocial, UUID> {

    List<UserSocial> findByUser(User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserSocial us WHERE us.user = :user")
    int deleteByUser(@Param("user") User user);
}
