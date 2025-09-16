package com._blog.backend.auth;

import java.io.IOException;
 
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;


@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthFilter() {
        this.jwtUtil = new JwtUtil();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = req.getHeader("Authorization");
        String accessToken = authHeader.substring(7);
            jwtUtil.extractUserName(accessToken);
            // System.out.println(accessToken);
            filterChain.doFilter(req, res);
        // if (!authHeader.isEmpty() && authHeader.startsWith("Bearer ")) {
           

        // } else {
        // filterChain.doFilter(req, res);
        // }
    }

}
