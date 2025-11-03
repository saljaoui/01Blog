package com._blog.backend.like;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com._blog.backend.auth.SecurityUtils;
import com._blog.backend.like.dto.LikeResponse;
import com._blog.backend.notification.NotificationService;
import com._blog.backend.post.Post;
import com._blog.backend.post.PostRepository;
import com._blog.backend.user.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;

    @Transactional
    public LikeResponse toggleLike(UUID postId) {
        User user = SecurityUtils.getCurrentUser();

        if (postId == null) {
            throw new IllegalStateException("User must be authenticated to like a post");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        boolean alreadyLiked = likeRepository.existsByPostAndUser(post, user);

        if (alreadyLiked) {
            likeRepository.deleteByPostAndUser(post, user);
        } else {
            Like like = Like.builder()
                    .post(post)
                    .user(user)
                    .build();
            if (like == null) {
                throw new IllegalArgumentException("Like entity cannot be null");
            }
            likeRepository.save(like);

            // Create notification for the post author
            notificationService.createLikeNotification(postId, user.getUsername());
        }

        long likesCount = likeRepository.countByPost(post);

        return LikeResponse.builder()
                .liked(!alreadyLiked)
                .likesCount(likesCount)
                .build();
    }

    public LikeResponse getLikeStatus(UUID postId) {
        User user = SecurityUtils.getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        boolean isLiked = likeRepository.existsByPostAndUser(post, user);
        long likesCount = likeRepository.countByPost(post);

        return LikeResponse.builder()
                .liked(isLiked)
                .likesCount(likesCount)
                .build();
    }

    public long getLikeCount(UUID postId) {
        if (postId == null) {
            throw new IllegalArgumentException("Post ID cannot be null");
        }
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        return likeRepository.countByPost(post);
    }
}
