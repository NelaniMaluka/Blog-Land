package com.nelani.blog_land_backend.util.validation;

import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.PostRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostValidation {
    private final PostRepository postRepository;

    public PostValidation(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post assertPostExist(long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post does not exist."));
    }

    public void assertPostBelongsToUser(Post post, User user) {
        if (!post.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "The user ID provided does not match the author's id . Please verify your credentials.");
        }
    }

    public void assertUserHasPostWithSameTitle(List<Post> posts, String title) {
        for (Post post : posts) {
            if (post.getTitle().equals(title)) {
                throw new IllegalArgumentException("You cannot use the same title twice.");
            }
        }
    }

}
