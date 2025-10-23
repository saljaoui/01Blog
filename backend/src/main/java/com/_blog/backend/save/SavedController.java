package com._blog.backend.save;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/posts")
    public ResponseEntity<java.util.List<com._blog.backend.post.dto.PostResponse>> getSavedPosts() {
        com._blog.backend.user.User user = com._blog.backend.auth.SecurityUtils.getCurrentUser();
        return ResponseEntity.ok(savedPostService.getSavedPostsByUser(user).stream()
                .map(post -> {
                    com._blog.backend.post.dto.PostResponse response = com._blog.backend.post.PostService.toPostResponse(post, user);
                    response.setLikesCount(savedPostService.getLikeRepository().countByPost(post));
                    response.setLiked(savedPostService.getLikeRepository().existsByPostAndUser(post, user));
                    response.setSavesCount(savedPostService.getSavedRepository().countByPost(post));
                    response.setSaved(savedPostService.getSavedRepository().existsByPostAndUser(post, user));
                    response.setCommentsCount(savedPostService.getCommentRepository().countByPost(post));
                    return response;
                })
                .toList());
    }

}