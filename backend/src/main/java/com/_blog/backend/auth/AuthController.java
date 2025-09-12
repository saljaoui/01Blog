package com._blog.backend.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._blog.backend.user.User;
import com._blog.backend.user.UserService;
import com._blog.backend.user.dto.UserRequest;
import com._blog.backend.user.dto.UserResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private List<User> users = new ArrayList<>();

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody UserRequest request) {

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        UserResponse savedUser = userService.createUser(request);
        JwtUtil jwtUtil = new JwtUtil();
        String token = jwtUtil.generateToken(savedUser);

        // System.out.println(token);

        return Map.of(
            "message", "Login successful",
            "token", token
        );
    }

    @PostMapping("/login")
    public List<User> login(@RequestBody Map<String, String> body) {
        // String username = body.get("username");
        // String password = body.get("password");
        // User user = new User(username, password);
        // users.add(user);
        return users;
    }
}
