package com.nelani.blog_land_backend.sockets;

import com.nelani.blog_land_backend.repository.CategoryRepository;
import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.response.CategoryResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategorySocket {

    private final PostRepository postRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public CategorySocket(PostRepository postRepository, SimpMessagingTemplate messagingTemplate) {
        this.postRepository = postRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public void updateCategories(CategoryRepository categoryRepository) {
        List<CategoryResponse> categories = categoryRepository.findAll()
                .stream()
                .map(category -> {
                    int postCount = postRepository.countByCategoryId(category.getId());
                    return new CategoryResponse(category.getId(),category.getName(), postCount);
                })
                .collect(Collectors.toList());

        messagingTemplate.convertAndSend("/topic/category", categories);
    }

}
