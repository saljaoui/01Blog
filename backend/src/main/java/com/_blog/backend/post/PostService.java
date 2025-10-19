package com._blog.backend.post;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.query.Page;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com._blog.backend.auth.SecurityUtils;
import com._blog.backend.post.dto.PostRequest;
import com._blog.backend.post.dto.PostResponse;
import com._blog.backend.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final ObjectMapper objectMapper;

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
        UUID currentUserId = SecurityUtils.getCurrentUser().getId();

        return postRepository.findAll().stream()
            .map(post -> PostResponse
            .builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .authorId(post.getUser().getId())
            .authorFirstName(post.getUser().getFirstName())
            .authorLastName(post.getUser().getLastName())
            .createdAt(post.getCreatedAt())
            .updatedAt(post.getUpdatedAt())
            .isOwner(post.getUser().getId().equals(currentUserId))
            .build()
            )
            .toList();
    }
}
