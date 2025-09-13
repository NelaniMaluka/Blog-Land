package com.nelani.blog_land_backend.Util.Sockets;

import com.nelani.blog_land_backend.dto.CategoryDto;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.repository.CategoryRepository;
import com.nelani.blog_land_backend.repository.LikeRepository;
import com.nelani.blog_land_backend.repository.PostRepository;
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
        List<CategoryDto> categories = categoryRepository.findAll()
                .stream()
                .map(category -> {
                    int postCount = postRepository.countByCategoryId(category.getId());
                    return new CategoryDto(category, postCount);
                })
                .collect(Collectors.toList());

        messagingTemplate.convertAndSend("/topic/category", categories);
    }

}
