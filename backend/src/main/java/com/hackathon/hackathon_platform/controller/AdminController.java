package com.hackathon.hackathon_platform.controller;

import com.hackathon.hackathon_platform.dto.*;
import com.hackathon.hackathon_platform.security.CustomUserDetails;
import com.hackathon.hackathon_platform.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ---- Utilisateurs ----

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request) {
        return ResponseEntity.ok(adminService.updateUserRole(id, request));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        adminService.deleteUser(id, userDetails.getUser());
        return ResponseEntity.ok("Utilisateur supprimé");
    }

    // ---- Équipes ----

    @DeleteMapping("/teams/{id}")
    public ResponseEntity<String> deleteTeam(@PathVariable Long id) {
        adminService.deleteTeam(id);
        return ResponseEntity.ok("Équipe supprimée");
    }

    // ---- Projets ----

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id) {
        adminService.deleteProject(id);
        return ResponseEntity.ok("Projet supprimé");
    }
}