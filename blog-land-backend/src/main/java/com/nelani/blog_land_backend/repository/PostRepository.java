package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.Post;

import com.nelani.blog_land_backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
  long count();

  // Count only published posts in category
  @Query("SELECT COUNT(p) FROM Post p WHERE p.category.id = :categoryId AND " +
      "p.isDraft = false AND (p.scheduledAt IS NULL OR p.scheduledAt <= CURRENT_TIMESTAMP)")
  int countByCategoryId(@Param("categoryId") UUID categoryId);

  List<Post> findAllByUserId(UUID userId);

  @Query("SELECT p FROM Post p WHERE p.isDraft = true AND p.scheduledAt <= CURRENT_TIMESTAMP")
  List<Post> findPostsToPublish();

  // Random published post
  @Query(value = """
          SELECT * FROM BLOG_POSTS
          WHERE IS_DRAFT = FALSE
            AND (SCHEDULED_AT IS NULL OR SCHEDULED_AT <= NOW())
          ORDER BY RAND()
          LIMIT 1
      """, nativeQuery = true)
  Post findRandomPost();

  // 25 random published posts
  @Query(value = """
          SELECT * FROM BLOG_POSTS
          WHERE IS_DRAFT = FALSE
            AND (SCHEDULED_AT IS NULL OR SCHEDULED_AT <= NOW())
          ORDER BY RAND()
          LIMIT 25
      """, nativeQuery = true)
  List<Post> findRandomPosts();

  // Search only published posts
  @Query("SELECT p FROM Post p JOIN FETCH p.category c WHERE " +
      "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
      "LOWER(p.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
      "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
      "p.isDraft = false AND (p.scheduledAt IS NULL OR p.scheduledAt <= CURRENT_TIMESTAMP)")
  Page<Post> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

  // Formula for getting what's trending
  @Query("""
          SELECT p
          FROM Post p
          LEFT JOIN Like l ON l.post = p
          LEFT JOIN Comment c ON c.post = p
          WHERE p.isDraft = false
            AND (p.scheduledAt IS NULL OR p.scheduledAt <= CURRENT_TIMESTAMP)
          GROUP BY p
          ORDER BY (p.viewCount * 0.5 + COUNT(l) * 0.3 + COUNT(c) * 0.2) DESC
      """)
  Page<Post> findTrendingPosts(Pageable pageable);

  Page<Post> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

  @Modifying
  @Transactional
  @Query("DELETE FROM Post p WHERE p.user = :user")
  int deleteByUser(@Param("user") User user);

}
