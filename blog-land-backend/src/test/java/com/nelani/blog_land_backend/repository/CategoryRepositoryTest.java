package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.Category;
import com.nelani.blog_land_backend.model.Post;

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
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    public void init() {
        List<Post> posts = new ArrayList<>();
        category = Category.builder()
                .name("testCategory")
                .posts(posts)
                .build();
    }

    @Test
    public void CategoryRepository_FindById_ReturnsCategory() {
        // Act
        categoryRepository.save(category);

        // Retrieve
        var found = categoryRepository.findById(category.getId());
        Assertions.assertThat(found).isPresent();
        Category retrievedCategory = found.get();
        Assertions.assertThat(retrievedCategory).isNotNull();
        Assertions.assertThat(retrievedCategory.getId()).isEqualTo(category.getId());
        Assertions.assertThat(retrievedCategory.getName()).isEqualTo(category.getName());
    }

    @Test
    public void CategoryRepository_FindByName_ReturnsTrue() {
        // Act
        categoryRepository.save(category);

        // Retrieve
        boolean categoryCheck = categoryRepository.existsByName(category.getName());

        Assertions.assertThat(categoryCheck).isTrue();
    }

    @Test
    public void CategoryRepository_FindByName_ReturnsFalse() {
        // Retrieve
        boolean categoryCheck = categoryRepository.existsByName(category.getName());

        Assertions.assertThat(categoryCheck).isFalse();
    }
}
