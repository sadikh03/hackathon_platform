package com.hackathon.hackathon_platform.controller;

import com.hackathon.hackathon_platform.dto.JwtResponse;
import com.hackathon.hackathon_platform.dto.LoginRequest;
import com.hackathon.hackathon_platform.dto.RegisterRequest;
import com.hackathon.hackathon_platform.service.AuthService;
import jakarta.validation.Valid;
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
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("Utilisateur créé avec succès");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        JwtResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
