package com.hackathon.hackathon_platform.controller;

import com.hackathon.hackathon_platform.dto.*;
import com.hackathon.hackathon_platform.security.CustomUserDetails;
import com.hackathon.hackathon_platform.service.EvaluationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jury")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping("/evaluations/{projectId}")
    public ResponseEntity<EvaluationResponse> evaluate(
            @PathVariable Long projectId,
            @Valid @RequestBody EvaluationRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        EvaluationResponse response = evaluationService.evaluate(projectId, request, userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/evaluations/mine")
    public ResponseEntity<List<EvaluationResponse>> getMyEvaluations(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(evaluationService.getMyEvaluations(userDetails.getUser()));
    }

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectResponse>> getProjectsToEvaluate() {
        return ResponseEntity.ok(evaluationService.getProjectsToEvaluate());
    }

    @GetMapping("/evaluations/project/{projectId}")
    public ResponseEntity<List<EvaluationResponse>> getEvaluationsByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(evaluationService.getEvaluationsByProject(projectId));
    }
}