package com._blog.backend.save;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com._blog.backend.like.dto.LikeResponse;
import com._blog.backend.save.dto.SavedRequest;
import com._blog.backend.save.dto.SavedResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/saveds")
@RequiredArgsConstructor
public class SavedController {

    private final  SavedService savedPostService;

    @PostMapping
    public ResponseEntity<SavedResponse> toggleLike(@RequestBody SavedRequest savedRequest) {
        return ResponseEntity.ok(savedPostService.toggleSave(savedRequest.getPostId()));
    }

    @GetMapping
    public ResponseEntity<SavedResponse> getLikes(@RequestParam UUID postId) {
        return ResponseEntity.ok(savedPostService.getLikeStatus(postId));
    }

}