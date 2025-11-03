package com._blog.backend.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com._blog.backend.exception.UserAlreadyExistsException;
import com._blog.backend.auth.dto.AuthResponse;
import com._blog.backend.user.dto.UserRequest;
import com._blog.backend.user.UserRepository;
import com._blog.backend.user.UserService;
import com._blog.backend.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(UserRequest userRequest) {
        // --- 🔎 Simple validation checks ---
        if (userRequest.getUsername() == null || userRequest.getUsername().length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters long");
        }

        if (userRequest.getPassword() == null || userRequest.getPassword().length() < 7) {
            throw new IllegalArgumentException("Password must be at least 7 characters long");
        }

        if (userRequest.getFirstName() == null || userRequest.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }

        if (userRequest.getLastName() == null || userRequest.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }

        // --- 🧠 Uniqueness checks ---
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        User user = userService.createUser(userRequest);

        String accessToken = jwtUtil.generateToken(user);

        RefreshToken refreshToken = refreshTokenService.create(user);

        return new AuthResponse("Registration successful", accessToken, refreshToken.getTokenHash());
    }

    public AuthResponse login(UserRequest userRequest) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userRequest.getUsername(),
                        userRequest.getPassword()));

        User user = userRepository.findByUsername(userRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtUtil.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.create(user);

        return new AuthResponse("login successful", accessToken, refreshToken.getTokenHash());
    }

    public AuthResponse refreshToken(String refreshToken) {
        return new AuthResponse();
    }

}
