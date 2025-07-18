package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LlikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);
    Long countByPostId(Long postId);
}
