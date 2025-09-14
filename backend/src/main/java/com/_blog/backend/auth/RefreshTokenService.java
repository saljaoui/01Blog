package com._blog.backend.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com._blog.backend.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken create(User user) {
        RefreshToken token = RefreshToken.builder()
                .uuid(UUID.randomUUID())
                .user(user)
                .tokenHash(UUID.randomUUID().toString())
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();

        return refreshTokenRepository.save(token);
    }
}
