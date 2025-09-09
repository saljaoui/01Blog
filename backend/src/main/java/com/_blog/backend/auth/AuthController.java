package com._blog.backend.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.apache.naming.java.javaURLContextFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._blog.backend.user.User;
// import com._blog.backend.auth.;;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private List<User> users = new ArrayList<>();

    @PostMapping("/register")
    public List<User> register(@RequestBody Map<String, String> body) {
        


        String username = body.get("username");
        String password = body.get("password");
        User user = new User((long) users.size() ,username, password);

        // JwtKeys key = new JwtKeys();
        JwtUtil jwtUtil = new JwtUtil();
        String token = jwtUtil.generateToken(user);

        System.out.println(token);

        users.add(user);
        return users;
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
