package com.hackathon.hackathon_platform.service;

import com.hackathon.hackathon_platform.dto.*;
import com.hackathon.hackathon_platform.entity.*;
import com.hackathon.hackathon_platform.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final SettingsService settingsService;
    private final FileStorageService fileStorageService;

    public ProjectService(ProjectRepository projectRepository,
                          TeamMemberRepository teamMemberRepository,
                          TeamRepository teamRepository,
                          SettingsService settingsService, FileStorageService fileStorageService) {
        this.projectRepository = projectRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.teamRepository = teamRepository;
        this.settingsService = settingsService;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public ProjectResponse uploadFile(Long projectId, MultipartFile file, User currentUser) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Projet introuvable"));

        checkIsTeamMember(project.getTeam().getId(), currentUser);

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        String storedPath = fileStorageService.store(file);

        project.setFileName(file.getOriginalFilename());
        project.setFilePath(storedPath);
        project = projectRepository.save(project);

        return mapToResponse(project);
    }

    public Project getProjectEntity(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Projet introuvable"));
    }

    @Transactional
    public ProjectResponse submitProject(ProjectRequest request, User currentUser) {

        // Règle 3 : on vérifie la deadline AVANT toute autre chose
        settingsService.checkDeadlineNotPassed();

        // On retrouve l'équipe du membre courant (rappel : une seule équipe possible à la fois)
        TeamMember membership = teamMemberRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Vous devez appartenir à une équipe pour soumettre un projet"));

        Team team = membership.getTeam();

        // Règle 2 : une équipe ne peut soumettre qu'un seul projet
        if (projectRepository.existsByTeamId(team.getId())) {
            throw new IllegalStateException(
                    "Votre équipe a déjà soumis un projet. Utilisez la modification (PUT).");
        }

        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .githubLink(request.getGithubLink())
                .team(team)
                .build();

        project = projectRepository.save(project);

        return mapToResponse(project);
    }

    @Transactional
    public ProjectResponse updateProject(Long projectId, ProjectRequest request, User currentUser) {

        // Règle 3 : deadline vérifiée aussi pour la modification
        settingsService.checkDeadlineNotPassed();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Projet introuvable"));

        // Règle 1 : seul un membre de l'équipe propriétaire peut modifier
        checkIsTeamMember(project.getTeam().getId(), currentUser);

        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setGithubLink(request.getGithubLink());

        project = projectRepository.save(project);

        return mapToResponse(project);
    }

    // Règle 4 : consultation libre, aucune vérification de droits
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ProjectResponse getProjectById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Projet introuvable"));
        return mapToResponse(project);
    }

    // Vérifie que l'utilisateur courant appartient bien à l'équipe propriétaire du projet
    private void checkIsTeamMember(Long teamId, User currentUser) {
        TeamMember membership = teamMemberRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new IllegalStateException("Vous n'appartenez à aucune équipe"));

        if (!membership.getTeam().getId().equals(teamId)) {
            throw new IllegalStateException(
                    "Vous ne pouvez modifier que le projet de votre propre équipe");
        }
    }

    // Transforme une entité Project en DTO sûr à renvoyer au client
    private ProjectResponse mapToResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getGithubLink(),
                project.getTeam().getId(),
                project.getTeam().getName(),
                project.getFileName()
        );
    }

    @Transactional
    public ProjectResponse deleteFile(Long projectId, User currentUser) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Projet introuvable"));

        checkIsTeamMember(project.getTeam().getId(), currentUser);

        if (project.getFilePath() == null) {
            throw new IllegalStateException("Aucun fichier attaché à ce projet");
        }

        fileStorageService.delete(project.getFilePath());

        project.setFileName(null);
        project.setFilePath(null);
        project = projectRepository.save(project);

        return mapToResponse(project);
    }
}
