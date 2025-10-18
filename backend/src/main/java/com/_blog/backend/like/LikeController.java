package com._blog.backend.like;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com._blog.backend.like.dto.LikeResponse;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/toggle")
    public ResponseEntity<LikeResponse> toggleLike(@RequestParam UUID postId) {
        return ResponseEntity.ok(likeService.toggleLike(postId));
    }

    @GetMapping
    public ResponseEntity<LikeResponse> getLikes(@RequestParam UUID postId) {
        return ResponseEntity.ok(likeService.getLikeStatus(postId));
    }

}