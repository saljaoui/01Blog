package com._blog.backend.user;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com._blog.backend.user.dto.UserRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createUser(UserRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .id(UUID.randomUUID())
                .build();

        userRepository.save(user);
        return user;
    }
}
