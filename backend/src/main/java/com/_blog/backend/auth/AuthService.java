package com._blog.backend.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com._blog.backend.exception.BadRequestException;
import com._blog.backend.exception.InvalidCredentialsException;
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
        // Validation checks
        validateRegistrationInput(userRequest);

        // Uniqueness checks
        checkUserUniqueness(userRequest);

        // Create user
        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        User user = userService.createUser(userRequest);

        // Generate tokens
        String accessToken = jwtUtil.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.create(user);

        return new AuthResponse(
                "Registration successful",
                accessToken,
                refreshToken.getTokenHash());
    }

    public AuthResponse login(UserRequest userRequest) {

        // Validate input
        if (userRequest.getUsername() == null || userRequest.getPassword() == null) {
            throw new BadRequestException("Username and password must be provided");
        }

        if (userRequest.getUsername().isEmpty() || userRequest.getPassword().isEmpty()) {
            throw new BadRequestException("Username and password cannot be empty");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userRequest.getUsername(),
                            userRequest.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        User user = userRepository.findByUsername(userRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtUtil.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.create(user);

        return new AuthResponse("login successful", accessToken, refreshToken.getTokenHash());
    }

    public AuthResponse refreshToken(String refreshToken) {
        return new AuthResponse();
    }

    private void validateRegistrationInput(UserRequest userRequest) {
        if (userRequest.getUsername() == null || userRequest.getUsername().length() < 3) {
            throw new BadRequestException("Username must be at least 3 characters long");
        }

        if (userRequest.getPassword() == null || userRequest.getPassword().length() < 7) {
            throw new BadRequestException("Password must be at least 7 characters long");
        }

        if (userRequest.getFirstName() == null || userRequest.getFirstName().trim().isEmpty()) {
            throw new BadRequestException("First name cannot be empty");
        }

        if (userRequest.getLastName() == null || userRequest.getLastName().trim().isEmpty()) {
            throw new BadRequestException("Last name cannot be empty");
        }

        if (userRequest.getEmail() == null || userRequest.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email cannot be empty");
        }
    }

    private void checkUserUniqueness(UserRequest userRequest) {
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }
    }

}
