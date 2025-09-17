package com._blog.backend.post;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._blog.backend.post.dto.PostResponse;

@RestController
@RequestMapping("/posts")
public class PostController {
    @GetMapping
    public ResponseEntity<PostResponse> createPost() {
        return ResponseEntity.ok(new PostResponse("is workinkg"));
    }
}