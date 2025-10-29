package com._blog.backend.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._blog.backend.user.dto.UserResponse;
import com._blog.backend.user.dto.UpdateProfileRequest;

import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUserProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getUserProfileWithStats(user));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getUserProfileByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        System.out.println(">>>>>>>username:");
        System.out.println(username);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userService.getUserProfileWithStats(user));
    }

    

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String username) {
        List<User> users = userService.searchUsersByUsername(username);
        List<UserResponse> userResponses = users.stream()
                .map(userService::getUserProfileWithStats)
                .toList();
        return ResponseEntity.ok(userResponses);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(@AuthenticationPrincipal User user, @RequestBody UpdateProfileRequest request) {
        User updatedUser = userService.updateProfile(user, request);
        return ResponseEntity.ok(userService.getUserProfileWithStats(updatedUser));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
