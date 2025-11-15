package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Comment comment;
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

        Category category = Category.builder()
                .name("testCategory")
                .build();

        post = Post.builder()
                .title("Tile")
                .summary("summary")
                .imgUrl("imgUrl")
                .user(user)
                .category(category)
                .build();
        post.setContent("This is some example content for the blog post.");

        // Save user and category
        userRepository.save(user);
        categoryRepository.save(category);
        postRepository.save(post);

        comment = Comment.builder()
                .content("content")
                .post(post)
                .user(user)
                .build();
    }

    @Test
    public void CommentRepository_CountByPost_ReturnComment() {
        // Act
        commentRepository.save(comment);

        // Assert
        long found = commentRepository.countByPost(post);
        Assertions.assertThat(found).isEqualTo(1);
    }

    @Test
    public void CommentRepository_FindById_ReturnComment() {
        // Act
        Comment savedComment = commentRepository.save(comment);

        // Assert
        var comment = commentRepository.findById(savedComment.getId());
        Assertions.assertThat(comment).isPresent();
        Comment foundComment = comment.get();
        Assertions.assertThat(foundComment.getId()).isEqualTo(savedComment.getId());
        Assertions.assertThat(foundComment.getContent()).isEqualTo(savedComment.getContent());
        Assertions.assertThat(foundComment.getUser()).isEqualTo(user);
    }

    @Test
    public void CommentRepository_FindByPostId_ReturnCommentPage() {
        // Arrange
        Pageable page = PageRequest.of(0, 5);

        // Act
        commentRepository.save(comment);

        // Assert
        var found = commentRepository.findByPostId(post.getId(), page);
        Assertions.assertThat(found).isNotNull();
        Assertions.assertThat(found.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(found.getContent()).hasSize(1);
        Assertions.assertThat(found.getContent().get(0).getUser().getId()).isEqualTo(user.getId());
        Assertions.assertThat(found.getContent().get(0).getPost().getId()).isEqualTo(post.getId());
    }

    @Test
    public void CommentRepository_FindByUserIdAndPostId_ReturnCommentPage() {
        // Act
        Comment savedComment = commentRepository.save(comment);

        // Assert
        var found = commentRepository.findByUserIdAndPostId(user.getId(), post.getId());
        Assertions.assertThat(found).isNotNull();
        Assertions.assertThat(found.size()).isEqualTo(1);
        Assertions.assertThat(found.get(0).getId()).isEqualTo(savedComment.getId());
        Assertions.assertThat(found.get(0).getUser().getId()).isEqualTo(savedComment.getUser().getId());
        Assertions.assertThat(found.get(0).getPost().getId()).isEqualTo(savedComment.getPost().getId());
    }

    @Test
    public void CommentRepository_DeleteByUser_ReturnCount() {
        // Act
        commentRepository.save(comment);

        // Assert
        var count = commentRepository.deleteByUser(user);
        Assertions.assertThat(count).isEqualTo(1);
    }

    @Test
    public void CommentRepository_DeleteByPost_ReturnCount() {
        // Act
        commentRepository.save(comment);

        // Assert
        var count = commentRepository.deleteByPost(post);
        Assertions.assertThat(count).isEqualTo(1);
    }

}
