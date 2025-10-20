package com._blog.backend.save;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com._blog.backend.like.dto.LikeResponse;
import com._blog.backend.save.dto.SavedPostResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/saveds")
@RequiredArgsConstructor
public class SavedPostController {

    private final  SavedPostService savedPostService;

    @PostMapping
    public ResponseEntity<SavedPostResponse> toggleLike(@RequestParam UUID postId) {
        return ResponseEntity.ok(savedPostService.toggleSave(postId));
    }

    @GetMapping
    public ResponseEntity<SavedPostResponse> getLikes(@RequestParam UUID postId) {
        return ResponseEntity.ok(savedPostService.getLikeStatus(postId));
    }

}