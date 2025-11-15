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
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private Category category;
    private Category category1;
    List<Post> postList;

    @BeforeEach
    public void init() {
        User user = User.builder()
                .firstname("firstname")
                .lastname("lastname")
                .email("john@example.com")
                .provider(Provider.LOCAL)
                .build();

        category = Category.builder()
                .name("testCategory")
                .build();

        // Save user and category
        userRepository.save(user);
        categoryRepository.save(category);

        postList = new ArrayList<>();
        postList.add(createPost("Title", user, category));
        postList.add(createPost("Title1", user, category));
        postList.add(createPost("Title2", user, category));
        postList.add(createPost("Title3", user, category));
        postList.add(createPost("Title4", user, category));

        category1 = Category.builder()
                .name("testCategory1")
                .build();
    }

    @Test
    public void CategoryRepository_FindById_ReturnsCategory() {
        // Act
        categoryRepository.save(category1);

        // Retrieve
        var found = categoryRepository.findById(category1.getId());
        Assertions.assertThat(found).isPresent();
        Category retrievedCategory = found.get();
        Assertions.assertThat(retrievedCategory).isNotNull();
        Assertions.assertThat(retrievedCategory.getId()).isEqualTo(category1.getId());
        Assertions.assertThat(retrievedCategory.getName()).isEqualTo(category1.getName());
    }

    @Test
    public void CategoryRepository_FindByName_ReturnsTrue() {
        // Act
        categoryRepository.save(category1);

        // Retrieve
        boolean categoryCheck = categoryRepository.existsByName(category1.getName());

        Assertions.assertThat(categoryCheck).isTrue();
    }

    @Test
    public void CategoryRepository_FindByName_ReturnsFalse() {
        // Retrieve
        boolean categoryCheck = categoryRepository.existsByName(category1.getName());

        Assertions.assertThat(categoryCheck).isFalse();
    }

    @Test
    public void CategoryRepository_FindByCategoryId_ReturnPostPage() {
        // Arrange
        Pageable page = PageRequest.of(0, 5);
        postList.forEach(postRepository::save);

        // Act
        var found = categoryRepository.findByCategoryId(category.getId(), page);

        // Assert
        Assertions.assertThat(found).isNotNull();
        Assertions.assertThat(found.getContent()).hasSize(5);
        Assertions.assertThat(found.getTotalElements()).isGreaterThanOrEqualTo(5);
        Assertions.assertThat(found.getNumber()).isEqualTo(0); // page index check
        Assertions.assertThat(found.getSize()).isEqualTo(5); // page size check

        found.getContent().forEach(post -> {
            Assertions.assertThat(post.getId()).isNotNull();
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
