package com._blog.backend.save;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com._blog.backend.auth.SecurityUtils;
import com._blog.backend.post.Post;
import com._blog.backend.post.PostRepository;
import com._blog.backend.save.dto.SavedResponse;
import com._blog.backend.user.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SavedService {

    private final SavedRepository savedRepository;
    private final PostRepository postRepository;
    private final com._blog.backend.like.LikeRepository likeRepository;
    private final com._blog.backend.comment.CommentRepository commentRepository;

    @Transactional
    public SavedResponse toggleSave(UUID postId) {
        User user = SecurityUtils.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        boolean alreadySaved = savedRepository.existsByPostAndUser(post, user);

        if (alreadySaved) {
            // remove saved post
            savedRepository.deleteByPostAndUser(post, user);
        } else {
            // save post
            Saved saved = Saved.builder()
                    .post(post)
                    .user(user)
                    .build();
            savedRepository.save(saved);
        }

        long savedCount = savedRepository.countByPost(post);

        return SavedResponse.builder()
                .saved(!alreadySaved)
                .savesCount(savedCount)
                .build();
    }

    public SavedResponse getLikeStatus(UUID postId) {
        User user = SecurityUtils.getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        boolean isSaved = savedRepository.existsByPostAndUser(post, user);
        long savedsCount = savedRepository.countByPost(post);

        return SavedResponse.builder()
                .saved(isSaved)
                .savesCount(savedsCount)
                .build();
    }  

    public boolean isSavedByUser(Post post, User user) {
        return savedRepository.existsByPostAndUser(post, user);
    }

    public java.util.List<Post> getSavedPostsByUser(User user) {
        return savedRepository.findByUser(user).stream()
                .map(Saved::getPost)
                .toList();
    }

    public com._blog.backend.like.LikeRepository getLikeRepository() {
        return likeRepository;
    }

    public SavedRepository getSavedRepository() {
        return savedRepository;
    }

    public com._blog.backend.comment.CommentRepository getCommentRepository() {
        return commentRepository;
    }
}
