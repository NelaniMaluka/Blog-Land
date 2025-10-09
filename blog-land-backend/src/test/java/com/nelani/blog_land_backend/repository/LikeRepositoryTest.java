package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
public class LikeRepositoryTest {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Like like;
    private User user;
    private Post post;

    @BeforeEach
    public void init() {
        user = User.builder()
                .firstname("firstname")
                .lastname("lastname")
                .email("john@example.com")
                .provider(Provider.LOCAL)
                .build();

        List<Post> posts = new ArrayList<>();
        Category category = Category.builder()
                .name("testCategory")
                .posts(posts)
                .build();

        post = Post.builder()
                .title("Tile")
                .summary("summary")
                .imgUrl("imgUrl")
                .user(user)
                .category(category)
                .build();
        post.setContent("This is some example content for the blog post."); // ensures wordCount & readTime are
                                                                            // calculated

        // Save user and category
        userRepository.save(user);
        categoryRepository.save(category);
        postRepository.save(post);

        // Then create the like
        like = Like.builder()
                .user(user)
                .post(post)
                .build();
    }

    @Test
    public void LikeRepository_FindById_ReturnsLike() {
        // Act
        Like savedLike = likeRepository.save(like);

        // Assert
        var found = likeRepository.findById(savedLike.getId());
        Assertions.assertThat(found).isPresent();
        Like foundLike = found.get();
        Assertions.assertThat(foundLike).isNotNull();
        Assertions.assertThat(foundLike.getPost()).isEqualTo(post);
        Assertions.assertThat(foundLike.getUser()).isEqualTo(user);
    }

    @Test
    public void LikeRepository_CountByPost_ReturnLikeCount() {
        // Act
        likeRepository.save(like);

        // Assert
        long found = likeRepository.countByPost(post);
        Assertions.assertThat(found).isEqualTo(1);
    }

    @Test
    public void LikeRepository_FindAllByUser_ReturnUserLike() {
        // Act
        Like savedLike = likeRepository.save(like);

        // Assert
        var found = likeRepository.findAllByUser(user);
        Assertions.assertThat(found).isNotNull();
        Assertions.assertThat(found.size()).isEqualTo(1);
        Assertions.assertThat(found).first().isEqualTo(savedLike);
    }

    @Test
    public void LikeRepository_findByUserAndPost_ReturnUserLike() {
        // Act
        likeRepository.save(like);

        // Assert
        var found = likeRepository.findByUserAndPost(user, post);
        Assertions.assertThat(found).isPresent();
        Like foundLike = found.get();
        Assertions.assertThat(foundLike).isNotNull();
        Assertions.assertThat(foundLike.getPost()).isEqualTo(post);
        Assertions.assertThat(foundLike.getUser()).isEqualTo(user);
    }

    @Test
    public void LikeRepository_Delete_DeletesLike() {
        // Act
        likeRepository.save(like);
        likeRepository.delete(like);

        // Assert
        var found = likeRepository.findByUserAndPost(user, post);
        Assertions.assertThat(found).isNotNull();
        Assertions.assertThat(found).isEmpty();
    }

}
