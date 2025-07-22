package com.serpentia.controller;

import com.serpentia.dto.AuthRequest;
import com.serpentia.dto.RegisterRequest;
import com.serpentia.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("Usuario registrado exitosamente");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {

        String token = authService.login(request);
        return ResponseEntity.ok("{\"token\": \"" + token + "\"}");

    }
} 