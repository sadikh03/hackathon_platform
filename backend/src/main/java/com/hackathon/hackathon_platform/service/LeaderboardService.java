package com.hackathon.hackathon_platform.service;

import com.hackathon.hackathon_platform.dto.LeaderboardEntryResponse;
import com.hackathon.hackathon_platform.entity.Evaluation;
import com.hackathon.hackathon_platform.entity.Project;
import com.hackathon.hackathon_platform.repository.EvaluationRepository;
import com.hackathon.hackathon_platform.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class LeaderboardService {

    private final ProjectRepository projectRepository;
    private final EvaluationRepository evaluationRepository;

    public LeaderboardService(ProjectRepository projectRepository,
                              EvaluationRepository evaluationRepository) {
        this.projectRepository = projectRepository;
        this.evaluationRepository = evaluationRepository;
    }

    public List<LeaderboardEntryResponse> getLeaderboard() {

        List<Project> projects = projectRepository.findAll();

        List<LeaderboardEntryResponse> entries = projects.stream()
                .map(this::computeEntry)
                // tri décroissant par score moyen
                .sorted(Comparator.comparingDouble(LeaderboardEntryResponse::getAverageScore).reversed())
                .toList();

        // on attribue le rang après le tri (impossible de le faire avant, on ne connaît pas encore l'ordre)
        return assignRanks(entries);
    }

    private LeaderboardEntryResponse computeEntry(Project project) {

        List<Evaluation> evaluations = evaluationRepository.findByProjectId(project.getId());

        double averageScore = evaluations.stream()
                .mapToDouble(Evaluation::getTotalScore)
                .average()
                .orElse(0.0); // aucune évaluation → score 0, pas de crash

        return new LeaderboardEntryResponse(
                0, // rang temporaire, sera corrigé dans assignRanks
                project.getTeam().getId(),
                project.getTeam().getName(),
                project.getId(),
                project.getTitle(),
                Math.round(averageScore * 100.0) / 100.0, // arrondi à 2 décimales
                evaluations.size()
        );
    }

    private List<LeaderboardEntryResponse> assignRanks(List<LeaderboardEntryResponse> sortedEntries) {
        for (int i = 0; i < sortedEntries.size(); i++) {
            sortedEntries.get(i).setRank(i + 1);
        }
        return sortedEntries;
    }
}
