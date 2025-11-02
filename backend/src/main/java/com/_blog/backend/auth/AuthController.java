package com._blog.backend.auth;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._blog.backend.auth.dto.AuthResponse;
import com._blog.backend.auth.dto.RefreshRequest;
import com._blog.backend.user.User;
import com._blog.backend.user.dto.UserRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshRequest refreshToken) {
        User user = refreshTokenService.validateRefreshToken(refreshToken.getRefreshToken());
        String newAccessToken = jwtUtil.generateToken(user);

        return ResponseEntity.ok(AuthResponse.builder()
                .message("Access token refreshed successfully")
                .accessToken(newAccessToken)
                .build());
    }
}
