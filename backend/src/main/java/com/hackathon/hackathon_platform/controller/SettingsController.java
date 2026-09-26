package com.hackathon.hackathon_platform.controller;

import com.hackathon.hackathon_platform.dto.*;
import com.hackathon.hackathon_platform.service.SettingsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    // Consultable par tout le monde (connecté) — utile pour l'afficher côté Angular
    @GetMapping("/api/settings/deadline")
    public ResponseEntity<DeadlineResponse> getDeadline() {
        return ResponseEntity.ok(settingsService.getDeadline());
    }

    // Modifiable uniquement par l'admin (protégé par /api/admin/** dans SecurityConfig)
    @PutMapping("/api/admin/settings/deadline")
    public ResponseEntity<DeadlineResponse> setDeadline(@Valid @RequestBody DeadlineRequest request) {
        return ResponseEntity.ok(settingsService.setDeadline(request));
    }
}
