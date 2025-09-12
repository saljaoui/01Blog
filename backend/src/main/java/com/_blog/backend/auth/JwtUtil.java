package com._blog.backend.auth;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com._blog.backend.user.User;
import com._blog.backend.user.dto.UserResponse;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    // @Value("${jwt.secret}")
    private String secret = "U2FtcGxlU2VjcmV0S2V5U2FtcGxlU2VjcmV0S2V5U2FtcGxlU2VjcmV0S2V5";

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateToken(UserResponse userResponse) {
        Instant now = Instant.now();
        SecretKey key = getSigningKey();

        return Jwts.builder()
                .subject(userResponse.getUuid().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(jwtExpirationMs)))
                .signWith(key)
                .compact();
    }
}