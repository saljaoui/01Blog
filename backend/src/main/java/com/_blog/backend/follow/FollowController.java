package com._blog.backend.follow;

import com._blog.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor 
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}")
    public ResponseEntity<Void> follow(@PathVariable String userId, @AuthenticationPrincipal User currentUser) {
        followService.follow(currentUser, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> unfollow(@PathVariable String userId, @AuthenticationPrincipal User currentUser) {
        followService.unfollow(currentUser, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/status")
    public ResponseEntity<Boolean> status(@PathVariable String userId, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(followService.isFollowing(currentUser, userId));
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<?>> followers(@PathVariable String userId) {
        return ResponseEntity.ok(followService.getFollowers(userId));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<?>> following(@PathVariable String userId) {
        return ResponseEntity.ok(followService.getFollowing(userId));
    }
}
