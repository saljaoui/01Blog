package com._blog.backend.user;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com._blog.backend.user.dto.UserRequest;
import com._blog.backend.user.dto.UserResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;

    public User createUser(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .uuid(UUID.randomUUID())
                .build();

        userRepository.save(user);
        return user;
    }

    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUuid(user.getUuid());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        return response;
    }
    
}
