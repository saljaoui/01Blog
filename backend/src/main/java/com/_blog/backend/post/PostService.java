package com._blog.backend.post;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com._blog.backend.auth.SecurityUtils;
import com._blog.backend.post.dto.PostRequest;
import com._blog.backend.post.dto.PostResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
    public PostResponse create(PostRequest userRequest) {

        UUID userId = SecurityUtils.getCurrentUserId();

        System.out.println(userId);
        System.out.println(userRequest);
        return new PostResponse("working");
    }
}
