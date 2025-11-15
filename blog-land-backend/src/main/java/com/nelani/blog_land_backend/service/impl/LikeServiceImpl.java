package com.nelani.blog_land_backend.service.impl;

import com.nelani.blog_land_backend.repository.PostRepository;
import com.nelani.blog_land_backend.cache.LikeCacheHelper;
import com.nelani.blog_land_backend.sockets.LikesSocket;
import com.nelani.blog_land_backend.util.validation.UserValidation;
import com.nelani.blog_land_backend.model.Like;
import com.nelani.blog_land_backend.model.Post;
import com.nelani.blog_land_backend.model.User;
import com.nelani.blog_land_backend.repository.LikeRepository;
import com.nelani.blog_land_backend.response.LikeResponse;
import com.nelani.blog_land_backend.service.LikeService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LikeServiceImpl implements LikeService {

        private final LikeRepository likeRepository;
        private final LikesSocket likesSocket;
        private final LikeCacheHelper likeCacheHelper;
        private final PostRepository postRepository;
        private final UserValidation userValidation;

        public LikeServiceImpl(LikeRepository likeRepository, LikesSocket likesSocket, LikeCacheHelper likeCacheHelper,
                        PostRepository postRepository, UserValidation userValidation) {
                this.likeRepository = likeRepository;
                this.likesSocket = likesSocket;
                this.likeCacheHelper = likeCacheHelper;
                this.postRepository = postRepository;
                this.userValidation = userValidation;
        }

        @Override
        @Transactional
        @Cacheable(value = "postLikesCount", key = "#postId")
        public long getPostLikesCount(UUID postId) {
                // Checks if the post exists
                Post existingPost = postRepository.findById(postId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Post not found."));

                return likeRepository.countByPost(existingPost);
        }

        @Override
        @Transactional
        @Cacheable(value = "userLikes", key = "T(org.springframework.security.core.context.SecurityContextHolder).context.authentication.name")
        public List<LikeResponse> getUserLikes() {
                // Get current authenticated user
                User user = userValidation.getAuthenticatedUser();

                // get user likes
                List<Like> userLikes = likeRepository.findAllByUser(user);

                // Format the response
                return userLikes.stream()
                                .map(like -> LikeResponse.builder()
                                                .likeId(like.getId())
                                                .likedAt(like.getLikedAt())
                                                .postId(like.getPost().getId())
                                                .build())
                                .toList();
        }

        @Override
        @Transactional
        public void addLike(UUID postId) {
                // Checks if the post exists
                Post existingPost = postRepository.findById(postId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Post not found."));

                // Get current authenticated user
                User user = userValidation.getAuthenticatedUser();

                // Check if the like already exists
                if (likeRepository.findByUserAndPost(user, existingPost).isPresent()) {
                        return;
                }

                Like like = Like.builder()
                                .post(existingPost)
                                .user(user)
                                .likedAt(LocalDateTime.now())
                                .build();

                likeRepository.save(like);

                // get user likes
                List<Like> userLikes = likeRepository.findAllByUser(user);

                // Format the response
                var userLikesList = userLikes.stream()
                                .map(l -> LikeResponse.builder()
                                                .likeId(l.getId())
                                                .likedAt(l.getLikedAt())
                                                .postId(l.getPost().getId())
                                                .build())
                                .toList();

                // update socket
                long count = likeRepository.countByPost(existingPost);
                likesSocket.updatePostLikes(count, existingPost.getId());
                likesSocket.updateUserLikes(user, userLikesList);

                likeCacheHelper.evictAllForPost(user.getEmail(), postId); // Evict Likes
        }

        @Override
        @Transactional
        public void removeLike(UUID likeId) {
                // Check if like exists
                Like like = likeRepository.findById(likeId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Like not found."));

                // Checks if the post exists
                Post post = postRepository.findById(like.getPost().getId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Post not found."));

                // Get current authenticated user
                User user = userValidation.getAuthenticatedUser();

                // Check ownership
                if (!like.getUser().getId().equals(user.getId())) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "You are not authorized to perform this action on this like.");
                }

                likeRepository.delete(like);

                // get user likes
                List<Like> userLikes = likeRepository.findAllByUser(user);

                // Format the response
                var userLikesList = userLikes.stream()
                                .map(l -> LikeResponse.builder()
                                                .likeId(l.getId())
                                                .likedAt(l.getLikedAt())
                                                .postId(l.getPost().getId())
                                                .build())
                                .toList();

                // update socket
                long count = likeRepository.countByPost(post);
                likesSocket.updatePostLikes(count, post.getId());
                likesSocket.updateUserLikes(user, userLikesList);

                likeCacheHelper.evictAllForPost(user.getEmail(), post.getId()); // Evict Likes
        }
}
