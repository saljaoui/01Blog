package com._blog.backend.follow;

import com._blog.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor 
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}")
    public ResponseEntity<Void> follow(@PathVariable UUID userId, @AuthenticationPrincipal User currentUser) {
        followService.follow(currentUser, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> unfollow(@PathVariable UUID userId, @AuthenticationPrincipal User currentUser) {
        followService.unfollow(currentUser, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/status")
    public ResponseEntity<Boolean> status(@PathVariable UUID userId, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(followService.isFollowing(currentUser, userId));
    }
}
