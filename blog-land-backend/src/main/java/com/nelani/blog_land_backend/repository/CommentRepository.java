package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.Comment;
import com.nelani.blog_land_backend.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    long countByPost(Post post);

    Page<Comment> findAll(Pageable pageable);

    Page<Comment> findByPostId(Long postId, Pageable pageable);

    List<Comment> findByUserIdAndPostId(Long userId, Long PostId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.user.id = :userId")
    Long countCommentsOnUserPosts(@Param("userId") Long userId);
}
