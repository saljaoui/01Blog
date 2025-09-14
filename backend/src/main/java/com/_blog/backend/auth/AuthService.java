package com._blog.backend.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com._blog.backend.auth.dto.AuthResponse;
import com._blog.backend.user.User;
import com._blog.backend.user.UserService;
import com._blog.backend.user.dto.UserRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    public AuthResponse register(UserRequest userRequest) {
        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        
        User user = userService.createUser(userRequest);

        String token = jwtUtil.generateToken(user);

        RefreshToken refreshToken = refreshTokenService.create(user);

        return new AuthResponse("Registration successful", token, refreshToken.getTokenHash());
    }
    
    public AuthResponse login(UserRequest userRequest) {
        return new AuthResponse();
    }
}
