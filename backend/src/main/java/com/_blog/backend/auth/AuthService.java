package com._blog.backend.auth;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com._blog.backend.auth.dto.AuthResponse;
import com._blog.backend.exception.InvalidCredentialsException;
import com._blog.backend.exception.UserAlreadyExistsException;
import com._blog.backend.user.User;
import com._blog.backend.user.UserRepository;
import com._blog.backend.user.UserService;
import com._blog.backend.user.dto.UserRequest;
import com._blog.backend.user.dto.UserResponse;

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
        System.out.println(userRequest);
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new UserAlreadyExistsException("Username is already taken");
        }

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email is already in use");
        }

        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        User user = userService.createUser(userRequest);

        String accessToken = jwtUtil.generateToken(user);

        RefreshToken refreshToken = refreshTokenService.create(user);

        return new AuthResponse("Registration successful", accessToken, refreshToken.getTokenHash());
    }

    public AuthResponse login(UserRequest userRequest) {
        Optional<User> user = userRepository.findByUsername(userRequest.getUsername());

        if (user.isPresent() && passwordEncoder.matches(userRequest.getPassword(), user.get().getPassword())) {
            String accessToken = jwtUtil.generateToken(user.get());
            RefreshToken refreshToken = refreshTokenService.create(user.get());

            return new AuthResponse("Registration successful", accessToken, refreshToken.getTokenHash());
        } else {
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }
}
