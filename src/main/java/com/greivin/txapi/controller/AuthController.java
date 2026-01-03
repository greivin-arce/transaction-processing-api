package com.greivin.txapi.controller;

import com.greivin.txapi.security.JwtService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/token")
    public Map<String, String> token() {
        String token = jwtService.generateToken(
                "test-user",
                Map.of("role", "USER")
        );
        return Map.of("token", token);
    }
}