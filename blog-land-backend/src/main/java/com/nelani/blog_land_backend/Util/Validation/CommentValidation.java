package com.nelani.blog_land_backend.util.validation;

import com.nelani.blog_land_backend.model.Comment;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.CommentRepository;
import org.springframework.stereotype.Component;

@Component
public class CommentValidation {
    private final CommentRepository commentRepository;

    public CommentValidation(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment assertCommentExist(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment does not exist."));
    }

    public void assertCommentBelongsToUser(Comment comment, User user) {
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "The user ID provided does not match the author's id . Please verify your credentials.");
        }
    }

}
