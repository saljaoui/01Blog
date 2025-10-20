package com._blog.backend.post;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com._blog.backend.auth.SecurityUtils;
import com._blog.backend.like.LikeRepository;
import com._blog.backend.post.dto.PostRequest;
import com._blog.backend.post.dto.PostResponse;
import com._blog.backend.save.SavedRepository;
import com._blog.backend.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final SavedRepository savedRepository;

    public PostResponse create(PostRequest postRequest) {
        User user = SecurityUtils.getCurrentUser();
        Post post = Post.builder()
                .id(UUID.randomUUID())
                .user(user)
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .build();
      
        postRepository.save(post);

        return new PostResponse();
    }
    
    public List<PostResponse> getAllPosts() {
        User user = SecurityUtils.getCurrentUser();
        UUID currentUserId = user.getId();

        return postRepository.findAll().stream()
            .map(post -> PostResponse
            .builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .likesCount(likeRepository.countByPost(post))
            .liked(likeRepository.existsByPostAndUser(post, user))
            .savesCount(savedRepository.countByPost(post))
            .saved(savedRepository.existsByPostAndUser(post, user))
            .authorId(post.getUser().getId())
            .authorFirstName(post.getUser().getFirstName())
            .authorLastName(post.getUser().getLastName())
            .createdAt(post.getCreatedAt())
            .updatedAt(post.getUpdatedAt())
            .owner(post.getUser().getId().equals(currentUserId))
            .build()
            )
            .toList();
    }
}
