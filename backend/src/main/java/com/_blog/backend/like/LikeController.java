package com._blog.backend.like;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com._blog.backend.like.dto.LikeRequest;
import com._blog.backend.like.dto.LikeResponse;
import com._blog.backend.user.User;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<LikeResponse> toggleLike(@Valid @RequestBody LikeRequest likeRequest, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(likeService.toggleLike(likeRequest.getPostId(), user));
    }

    @GetMapping
    public ResponseEntity<LikeResponse> getLikes(@RequestParam UUID postId) {
        return ResponseEntity.ok(likeService.getLikeStatus(postId));
    }
}