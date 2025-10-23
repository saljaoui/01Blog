package com._blog.backend.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._blog.backend.auth.SecurityUtils;
import com._blog.backend.user.dto.UserResponse;

import lombok.RequiredArgsConstructor;

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
}
