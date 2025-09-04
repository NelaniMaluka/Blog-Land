package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.Category;
import com.nelani.blog_land_backend.model.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
  long count();

  long countByCategory(Category category);

  // Only published posts
  @Query("SELECT p FROM Post p WHERE p.isDraft = false AND " +
      "(p.scheduledAt IS NULL OR p.scheduledAt <= CURRENT_TIMESTAMP)")
  Page<Post> findPublishedPosts(Pageable pageable);

  @Query("SELECT p FROM Post p WHERE p.isDraft = true AND p.scheduledAt <= CURRENT_TIMESTAMP")
  List<Post> findPostsToPublish();

  // Random published post
  @Query(value = """
        SELECT * FROM blog.BLOG_POSTS
        WHERE IS_DRAFT = FALSE
          AND (SCHEDULED_AT IS NULL OR SCHEDULED_AT <= NOW())
          AND ID >= (
              SELECT FLOOR(RAND() * (SELECT MAX(ID) FROM blog.BLOG_POSTS))
          )
        ORDER BY ID ASC
        LIMIT 1
    """, nativeQuery = true)
  Post findRandomPost();

  // Count only published posts in category
  @Query("SELECT COUNT(p) FROM Post p WHERE p.category.id = :categoryId AND " +
      "p.isDraft = false AND (p.scheduledAt IS NULL OR p.scheduledAt <= CURRENT_TIMESTAMP)")
  int countByCategoryId(@Param("categoryId") Long categoryId);

  // Search only published posts
  @Query("SELECT p FROM Post p JOIN FETCH p.category c WHERE " +
      "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
      "LOWER(p.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
      "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
      "p.isDraft = false AND (p.scheduledAt IS NULL OR p.scheduledAt <= CURRENT_TIMESTAMP)")
  Page<Post> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

  @Query("""
          SELECT p FROM Post p
          LEFT JOIN p.likes l
          WHERE p.isDraft = false
            AND (p.scheduledAt IS NULL OR p.scheduledAt <= CURRENT_TIMESTAMP)
          GROUP BY p
          ORDER BY (p.viewCount * 0.6 + COUNT(l) * 0.4) DESC
      """)
  Page<Post> findTrendingPosts(Pageable pageable);

  // Category filter, only published
  @Query("SELECT p FROM Post p WHERE p.category.id = :categoryId AND " +
      "p.isDraft = false AND (p.scheduledAt IS NULL OR p.scheduledAt <= CURRENT_TIMESTAMP)")
  Page<Post> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

  Page<Post> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

}
