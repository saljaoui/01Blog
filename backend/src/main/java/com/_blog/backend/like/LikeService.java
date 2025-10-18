package com._blog.backend.like;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com._blog.backend.auth.SecurityUtils;
import com._blog.backend.like.dto.LikeResponse;
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

    @Transactional
    public LikeResponse toggleLike(UUID postId) {
        User user = SecurityUtils.getCurrentUser();

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
            likeRepository.save(like);
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
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        return likeRepository.countByPost(post);
    }
}
