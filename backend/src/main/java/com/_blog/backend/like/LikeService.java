package com._blog.backend.like;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com._blog.backend.like.dto.LikeResponse;
import com._blog.backend.notification.NotificationService;
import com._blog.backend.post.Post;
import com._blog.backend.post.PostRepository;
import com._blog.backend.user.Role;
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
    public LikeResponse toggleLike(UUID postId, User user) {

        if (user == null) {
            throw new IllegalStateException("User must be authenticated to like a post");
        }

        if (postId == null) {
            throw new IllegalArgumentException("postId cannot be null");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Check if post is hidden and user is not admin
        if (post.isHidden() && !user.getRole().equals(Role.ADMIN)) {
            throw new IllegalStateException("Cannot like hidden post");
        }

        boolean alreadyLiked = likeRepository.existsByPostAndUser(post, user);

        if (alreadyLiked) {
            likeRepository.deleteByPostAndUser(post, user);
        } else {
            Like like = Like.builder()
                    .post(post)
                    .user(user)
                    .build();

            likeRepository.save(like);

            notificationService.createLikeNotification(postId, user.getUsername());
        }

        long likesCount = likeRepository.countByPost(post);

        return LikeResponse.builder()
                .liked(!alreadyLiked)
                .likesCount(likesCount)
                .build();
    }

    public LikeResponse getLikeStatus(UUID postId, User user) {

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
