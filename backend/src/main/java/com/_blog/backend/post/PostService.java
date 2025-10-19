package com._blog.backend.post;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    
    public PostResponse getAllPosts() {
        List<Post> posts = postRepository.findAll();
        System.out.println(posts);
        // You can map posts to DTOs if needed
        return new PostResponse() ;
    }
}
