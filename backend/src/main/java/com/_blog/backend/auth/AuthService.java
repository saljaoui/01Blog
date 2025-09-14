package com._blog.backend.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com._blog.backend.auth.dto.AuthResponse;
import com._blog.backend.user.User;
import com._blog.backend.user.UserRepository;
import com._blog.backend.user.UserService;
import com._blog.backend.user.dto.UserRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    // private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthResponse register(UserRequest userRequest) {
        // if (userRepository.existsByUsername(userRequest.getUsername())) {
        //     throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        // }

        // if (userRepository.existsByEmail(userRequest.getEmail())) {
        //     throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use");
        // }

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
