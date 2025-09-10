package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.Category;
import com.nelani.blog_land_backend.model.Comment;
import com.nelani.blog_land_backend.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    long countByPost(Post post);

    Page<Comment> findAll(Pageable pageable);

    Page<Comment> findByPostId(Long postId, Pageable pageable);

    List<Comment> findByUserIdAndPostId(Long userId, Long PostId);
}
