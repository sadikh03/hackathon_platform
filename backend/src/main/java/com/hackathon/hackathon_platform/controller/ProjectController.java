package com.hackathon.hackathon_platform.controller;

import com.hackathon.hackathon_platform.dto.*;
import com.hackathon.hackathon_platform.security.CustomUserDetails;
import com.hackathon.hackathon_platform.service.FileStorageService;
import com.hackathon.hackathon_platform.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final FileStorageService fileStorageService;

    public ProjectController(ProjectService projectService, FileStorageService fileStorageService) {
        this.projectService = projectService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> submitProject(
            @Valid @RequestBody ProjectRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ProjectResponse response = projectService.submitProject(request, userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ProjectResponse response = projectService.updateProject(id, request, userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }
// ... dans la classe ProjectController :

    @PostMapping("/{id}/file")
    public ResponseEntity<ProjectResponse> uploadFile(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ProjectResponse response = projectService.uploadFile(id, file, userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable Long id) {

        var project = projectService.getProjectEntity(id);

        if (project.getFilePath() == null) {
            throw new IllegalArgumentException("Aucun fichier attaché à ce projet");
        }

        byte[] data = fileStorageService.read(project.getFilePath());
        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + project.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(data.length)
                .body(resource);
    }

    @DeleteMapping("/{id}/file")
    public ResponseEntity<ProjectResponse> deleteFile(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ProjectResponse response = projectService.deleteFile(id, userDetails.getUser());
        return ResponseEntity.ok(response);
    }
}
