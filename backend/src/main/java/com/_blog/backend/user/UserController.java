package com._blog.backend.user;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com._blog.backend.user.dto.UserRequest;
import com._blog.backend.user.dto.UserResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserResponse createUser(@RequestBody UserRequest request) {
        return userService.createUser(request);
    } 
}
