package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.Category;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.Provider;
import com.nelani.blog_land_backend.model.User;
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

import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private List<Post> postList;
    private User user;
    private Category category;

    @BeforeEach
    public void init() {
        postList = new ArrayList<>();

        user = User.builder()
                .firstname("firstname")
                .lastname("lastname")
                .email("john@example.com")
                .provider(Provider.LOCAL)
                .build();

        List<Post> posts = new ArrayList<>();
        category = Category.builder()
                .name("testCategory")
                .posts(posts)
                .build();

        // Save user and category
        User savedUser = userRepository.save(user);
        Category savedCategory = categoryRepository.save(category);

        postList.add(createPost("Title", savedUser, savedCategory));
        postList.add(createPost("Title1", savedUser, savedCategory));
        postList.add(createPost("Title2", savedUser, savedCategory));
        postList.add(createPost("Title3", savedUser, savedCategory));
        postList.add(createPost("Title4", savedUser, savedCategory));
    }

    @Test
    public void PostRepository_FindById_ReturnPost() {
        // Arrange
        Post post = createPost("Title", user, category);

        // Act
        Post savedPost = postRepository.save(post);

        // Assert
        var found = postRepository.findById(savedPost.getId());
        Assertions.assertThat(found).isPresent();
        Post foundPost = found.get();
        Assertions.assertThat(foundPost.getId()).isEqualTo(savedPost.getId());
        Assertions.assertThat(foundPost.getTitle()).isEqualTo(savedPost.getTitle());
        Assertions.assertThat(foundPost.getUser()).isEqualTo(savedPost.getUser());
        Assertions.assertThat(foundPost.getCategory()).isEqualTo(savedPost.getCategory());
    }

    @Test
    public void PostRepository_LongCount_ReturnPostCount() {
        // Arrange
        Post post = createPost("Title", user, category);

        // Act
        postRepository.save(post);

        // Assert
        long found = postRepository.count();
        Assertions.assertThat(found).isEqualTo(1);
    }

    @Test
    public void PostRepository_LongCountByCategoryId_ReturnPostCount() {
        // Arrange
        Post post = createPost("Title", user, category);

        // Act
        postRepository.save(post);

        // Assert
        long found = postRepository.countByCategoryId(category.getId());
        Assertions.assertThat(found).isEqualTo(1);
    }

    @Test
    public void PostRepository_LongCountByUser_ReturnPostCount() {
        // Arrange
        Post post = createPost("Title", user, category);

        // Act
        postRepository.save(post);

        // Assert
        long found = postRepository.countByUser(user);
        Assertions.assertThat(found).isEqualTo(1);
    }

    @Test
    public void PostRepository_FindAllByUserId_ReturnPostList() {
        // Act
        postList.forEach(postRepository::save);

        // Assert
        var found = postRepository.findAllByUserId(user.getId());
        Assertions.assertThat(found).isNotNull();
        Assertions.assertThat(found)
                .hasSize(5)
                .extracting(Post::getTitle)
                .contains("Title", "Title1", "Title2", "Title3", "Title4");
        found.forEach(post -> {
            Assertions.assertThat(post.getSummary()).isEqualTo("summary");
            Assertions.assertThat(post.getImgUrl()).isEqualTo("imgUrl");
        });
    }

    @Test
    public void PostRepository_FindRandomPost_ReturnPost() {
        // Act
        postList.forEach(postRepository::save);

        // Assert
        var found = postRepository.findRandomPost();
        Assertions.assertThat(found).isNotNull();
        Assertions.assertThat(found.getId()).isGreaterThan(0);
        Assertions.assertThat(found.getSummary()).isEqualTo("summary");
        Assertions.assertThat(found.getImgUrl()).isEqualTo("imgUrl");
    }

    @Test
    public void PostRepository_FindRandomPosts_ReturnPostList() {
        // Act
        postList.forEach(postRepository::save);

        // Act
        var found = postRepository.findRandomPosts();

        // Assert
        Assertions.assertThat(found).isNotNull();
        Assertions.assertThat(found).hasSize(5);
        found.forEach(post -> {
            Assertions.assertThat(post.getId()).isGreaterThan(0);
            Assertions.assertThat(post.getTitle()).isNotBlank();
            Assertions.assertThat(post.getSummary()).isEqualTo("summary");
            Assertions.assertThat(post.getImgUrl()).isEqualTo("imgUrl");
        });
    }

    @Test
    public void PostRepository_SearchByKeyword_ReturnPostPage() {
        // Arrange
        Pageable page = PageRequest.of(0, 5);
        postList.forEach(postRepository::save);

        // Act
        var found = postRepository.searchByKeyword("Title", page);

        // Assert
        Assertions.assertThat(found).isNotNull();
        Assertions.assertThat(found.getContent()).hasSize(5);
        Assertions.assertThat(found.getTotalElements()).isGreaterThanOrEqualTo(5);
        Assertions.assertThat(found.getNumber()).isEqualTo(0); // page index check
        Assertions.assertThat(found.getSize()).isEqualTo(5); // page size check

        found.getContent().forEach(post -> {
            Assertions.assertThat(post.getId()).isGreaterThan(0);
            Assertions.assertThat(post.getSummary()).isEqualTo("summary");
            Assertions.assertThat(post.getImgUrl()).isEqualTo("imgUrl");
        });
    }

    @Test
    public void PostRepository_FindTrendingPosts_ReturnPostPage() {
        // Arrange
        Pageable page = PageRequest.of(0, 5);
        postList.forEach(postRepository::save);

        // Act
        var found = postRepository.findTrendingPosts(page);

        // Assert
        Assertions.assertThat(found).isNotNull();
        Assertions.assertThat(found.getContent()).hasSize(5);
        Assertions.assertThat(found.getTotalElements()).isGreaterThanOrEqualTo(5);
        Assertions.assertThat(found.getNumber()).isEqualTo(0); // page index check
        Assertions.assertThat(found.getSize()).isEqualTo(5); // page size check

        found.getContent().forEach(post -> {
            Assertions.assertThat(post.getId()).isGreaterThan(0);
            Assertions.assertThat(post.getSummary()).isEqualTo("summary");
            Assertions.assertThat(post.getImgUrl()).isEqualTo("imgUrl");
        });
    }

    // @Test
    // public void PostRepository_FindByCategoryId_ReturnPostPage() {
    // // Arrange
    // Pageable page = PageRequest.of(0, 5);
    // postList.forEach(postRepository::save);
    //
    // // Act
    // var found = postRepository.findByCategoryId(category.getId(), page);
    //
    // // Assert
    // Assertions.assertThat(found).isNotNull();
    // Assertions.assertThat(found.getContent()).hasSize(5);
    // Assertions.assertThat(found.getTotalElements()).isGreaterThanOrEqualTo(5);
    // Assertions.assertThat(found.getNumber()).isEqualTo(0); // page index check
    // Assertions.assertThat(found.getSize()).isEqualTo(5); // page size check
    //
    // found.getContent().forEach(post -> {
    // Assertions.assertThat(post.getId()).isGreaterThan(0);
    // Assertions.assertThat(post.getSummary()).isEqualTo("summary");
    // Assertions.assertThat(post.getImgUrl()).isEqualTo("imgUrl");
    // });
    // }

    @Test
    public void PostRepository_FindByUserIdOrderByCreatedAtDesc_ReturnPostPage() {
        // Arrange
        Pageable page = PageRequest.of(0, 5);
        postList.forEach(postRepository::save);

        // Act
        var found = postRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), page);

        // Assert
        Assertions.assertThat(found).isNotNull();
        Assertions.assertThat(found.getContent()).hasSize(5);
        Assertions.assertThat(found.getTotalElements()).isGreaterThanOrEqualTo(5);
        Assertions.assertThat(found.getNumber()).isEqualTo(0); // page index check
        Assertions.assertThat(found.getSize()).isEqualTo(5); // page size check

        found.getContent().forEach(post -> {
            Assertions.assertThat(post.getId()).isGreaterThan(0);
            Assertions.assertThat(post.getSummary()).isEqualTo("summary");
            Assertions.assertThat(post.getImgUrl()).isEqualTo("imgUrl");
        });
    }

    private Post createPost(String title, User user, Category category) {
        Post post = Post.builder()
                .title(title)
                .summary("summary")
                .imgUrl("imgUrl")
                .user(user)
                .category(category)
                .build();
        post.setContent("This is some example content for the blog post."); // ensures wordCount & readTime are
                                                                            // calculated
        return post;
    }

}
