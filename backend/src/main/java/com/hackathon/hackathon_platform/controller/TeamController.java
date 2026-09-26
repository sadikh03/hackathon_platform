package com.hackathon.hackathon_platform.controller;

import com.hackathon.hackathon_platform.dto.*;
import com.hackathon.hackathon_platform.security.CustomUserDetails;
import com.hackathon.hackathon_platform.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(
            @Valid @RequestBody TeamRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        TeamResponse response = teamService.createTeam(request, userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<TeamResponse> joinTeam(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        TeamResponse response = teamService.joinTeam(id, userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<String> leaveTeam(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        teamService.leaveTeam(id, userDetails.getUser());
        return ResponseEntity.ok("Vous avez quitté l'équipe");
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeamById(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getTeamById(id));
    }
}
