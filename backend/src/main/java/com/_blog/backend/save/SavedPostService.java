package com._blog.backend.save;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com._blog.backend.auth.SecurityUtils;
import com._blog.backend.post.Post;
import com._blog.backend.post.PostRepository;
import com._blog.backend.save.dto.SavedPostResponse;
import com._blog.backend.user.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SavedPostService {
    
    private final SavedPostRepository savedPostRepository;
    private final PostRepository postRepository;

    @Transactional
    public SavedPostResponse toggleSave(UUID postId) {
        User user = SecurityUtils.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        boolean alreadySaved = savedPostRepository.existsByPostAndUser(post, user);

        if (alreadySaved) {
            // remove saved post
            savedPostRepository.deleteByPostAndUser(post, user);
        } else {
            // save post
            SavedPost saved = SavedPost.builder()
                    .post(post)
                    .user(user)
                    .build();
            savedPostRepository.save(saved);
        }

        long savedCount = savedPostRepository.countByPost(post); // optional

        return SavedPostResponse.builder()
                .saved(!alreadySaved)
                .savedCount(savedCount)
                .build();
    }

    public boolean isSavedByUser(Post post, User user) {
        return savedPostRepository.existsByPostAndUser(post, user);
    }
}
