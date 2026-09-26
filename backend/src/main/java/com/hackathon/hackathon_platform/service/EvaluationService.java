package com.hackathon.hackathon_platform.service;

import com.hackathon.hackathon_platform.dto.*;
import com.hackathon.hackathon_platform.entity.*;
import com.hackathon.hackathon_platform.repository.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final ProjectRepository projectRepository;
    private final NotificationService notificationService;
    private final TeamMemberRepository teamMemberRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final LeaderboardService leaderboardService;

    public EvaluationService(EvaluationRepository evaluationRepository,
                             ProjectRepository projectRepository, NotificationService notificationService, TeamMemberRepository teamMemberRepository, SimpMessagingTemplate messagingTemplate, LeaderboardService leaderboardService) {
        this.evaluationRepository = evaluationRepository;
        this.projectRepository = projectRepository;
        this.notificationService = notificationService;
        this.teamMemberRepository = teamMemberRepository;
        this.messagingTemplate = messagingTemplate;
        this.leaderboardService = leaderboardService;
    }

    @Transactional
    public EvaluationResponse evaluate(Long projectId, EvaluationRequest request, User jury) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Projet introuvable"));

        boolean isNewEvaluation = evaluationRepository
                .findByProjectIdAndJuryId(projectId, jury.getId())
                .isEmpty();

        Evaluation evaluation = evaluationRepository
                .findByProjectIdAndJuryId(projectId, jury.getId())
                .orElse(Evaluation.builder()
                        .project(project)
                        .jury(jury)
                        .build());

        evaluation.setInnovationScore(request.getInnovationScore());
        evaluation.setTechnicalScore(request.getTechnicalScore());
        evaluation.setPresentationScore(request.getPresentationScore());
        evaluation.setComment(request.getComment());

        evaluation = evaluationRepository.save(evaluation);

        if (isNewEvaluation) {
            List<TeamMember> members = teamMemberRepository.findByTeamId(project.getTeam().getId());
            for (TeamMember member : members) {
                notificationService.notify(member.getUser(),
                        "Votre projet \"" + project.getTitle() + "\" a été noté par un jury");
            }
        }

        // NOUVEAU : diffuse le leaderboard à jour à tous les clients connectés
        messagingTemplate.convertAndSend("/topic/leaderboard", leaderboardService.getLeaderboard());

        return mapToResponse(evaluation);
    }

    // Règle 4 : le jury consulte tous les projets à évaluer
    public List<ProjectResponse> getProjectsToEvaluate() {
        return projectRepository.findAll()
                .stream()
                .map(p -> new ProjectResponse(
                        p.getId(),
                        p.getTitle(),
                        p.getDescription(),
                        p.getGithubLink(),
                        p.getTeam().getId(),
                        p.getTeam().getName(),
                        p.getFileName()
                ))
                .toList();
    }

    // Toutes les évaluations reçues par un projet (utile pour le leaderboard plus tard)
    public List<EvaluationResponse> getEvaluationsByProject(Long projectId) {
        return evaluationRepository.findByProjectId(projectId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private EvaluationResponse mapToResponse(Evaluation evaluation) {
        return new EvaluationResponse(
                evaluation.getId(),
                evaluation.getInnovationScore(),
                evaluation.getTechnicalScore(),
                evaluation.getPresentationScore(),
                evaluation.getTotalScore(), // méthode @Transient déjà définie dans l'entité
                evaluation.getComment(),
                evaluation.getProject().getId(),
                evaluation.getProject().getTitle(),
                evaluation.getJury().getUsername()
        );
    }

    public List<EvaluationResponse> getMyEvaluations(User jury) {
        return evaluationRepository.findByJuryId(jury.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
}
