package com.nelani.blog_land_backend.Util.Validation;

import com.nelani.blog_land_backend.model.Comment;
import com.nelani.blog_land_backend.model.User;

import java.util.Optional;

public class CommentValidation {

    public static void assertCommentExists(Optional<Comment> post) {
        if (post.isEmpty()) {
            throw new IllegalArgumentException("Comment does not exist.");
        }
    }

    public static void assertCommentBelongsToUser(Comment comment, User user) {
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "The user ID provided does not match the author's id . Please verify your credentials.");
        }
    }

}
