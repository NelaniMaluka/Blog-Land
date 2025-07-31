package com.nelani.blog_land_backend.Util.Validation;

import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.User;

import java.util.List;
import java.util.Optional;

public class PostValidation {

    public static void assertPostExists(Optional<Post> post){
        if (post.isEmpty()) {
            throw new IllegalArgumentException("Post does not exist.");
        }
    }

    public static void assertPostBelongsToUser(Post post, User user){
        if (!post.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("The user ID provided does not match the author's id . Please verify your credentials.");
        }
    }

    public static void assertUserHasPostWithSameTitle(List<Post> posts, String title){
        for (Post post : posts) {
            if (post.getTitle().equals(title)) {
                throw new IllegalArgumentException("You cannot use the same title twice");
            }
        }
    }

}
