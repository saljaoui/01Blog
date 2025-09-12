package com._blog.backend.user.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class UserResponse {
    private UUID uuid;
    private String username;
    private String email;
}