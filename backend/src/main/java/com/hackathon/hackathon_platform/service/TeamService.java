package com.hackathon.hackathon_platform.service;


import com.hackathon.hackathon_platform.dto.*;
import com.hackathon.hackathon_platform.entity.*;
import com.hackathon.hackathon_platform.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public TeamService(TeamRepository teamRepository,
                       TeamMemberRepository teamMemberRepository, NotificationService notificationService,
                       UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @Transactional
    public TeamResponse createTeam(TeamRequest request, User currentUser) {

        if (currentUser.getRole() != Role.ROLE_PARTICIPANT) {
            throw new IllegalStateException("Seuls les participants peuvent créer une équipe");
        }

        // Règle 1 : un participant déjà dans une équipe ne peut pas en créer une autre
        if (teamMemberRepository.findByUserId(currentUser.getId()).isPresent()) {
            throw new IllegalStateException("Vous êtes déjà membre d'une équipe");
        }

        if (teamRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Ce nom d'équipe est déjà utilisé");
        }

        Team team = Team.builder()
                .name(request.getName())
                .createdBy(currentUser)
                .build();
        team = teamRepository.save(team);

        // Le créateur rejoint automatiquement sa propre équipe
        TeamMember membership = TeamMember.builder()
                .user(currentUser)
                .team(team)
                .build();
        teamMemberRepository.save(membership);

        return mapToResponse(team);
    }

    @Transactional
    public TeamResponse joinTeam(Long teamId, User currentUser) {

        if (currentUser.getRole() != Role.ROLE_PARTICIPANT) {
            throw new IllegalStateException("Seuls les participants peuvent rejoindre une équipe");
        }

        if (teamMemberRepository.findByUserId(currentUser.getId()).isPresent()) {
            throw new IllegalStateException("Vous êtes déjà membre d'une équipe");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Équipe introuvable"));

        TeamMember membership = TeamMember.builder()
                .user(currentUser)
                .team(team)
                .build();
        teamMemberRepository.save(membership);

        // Notifie les membres déjà présents (pas le nouveau membre lui-même)
        List<TeamMember> existingMembers = teamMemberRepository.findByTeamId(teamId);
        for (TeamMember member : existingMembers) {
            if (!member.getUser().getId().equals(currentUser.getId())) {
                notificationService.notify(member.getUser(),
                        currentUser.getUsername() + " a rejoint votre équipe " + team.getName());
            }
        }

        return mapToResponse(team);
    }

    @Transactional
    public void leaveTeam(Long teamId, User currentUser) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Équipe introuvable"));

        TeamMember membership = teamMemberRepository.findByUserAndTeam(currentUser, team)
                .orElseThrow(() -> new IllegalStateException("Vous n'êtes pas membre de cette équipe"));

        teamMemberRepository.delete(membership);

        // On récupère les membres restants AVANT de décider de supprimer l'équipe
        List<TeamMember> remainingMembers = teamMemberRepository.findByTeamId(teamId);

        if (remainingMembers.isEmpty()) {
            teamRepository.delete(team);
        } else {
            // Notifie les membres restants que quelqu'un a quitté
            for (TeamMember member : remainingMembers) {
                notificationService.notify(member.getUser(),
                        currentUser.getUsername() + " a quitté votre équipe " + team.getName());
            }
        }
    }

    public List<TeamResponse> getAllTeams() {
        return teamRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TeamResponse getTeamById(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Équipe introuvable"));
        return mapToResponse(team);
    }

    // Transforme une entité Team en DTO sûr à renvoyer au client
    private TeamResponse mapToResponse(Team team) {
        List<TeamMemberResponse> members = teamMemberRepository.findByTeamId(team.getId())
                .stream()
                .map(tm -> new TeamMemberResponse(tm.getUser().getId(), tm.getUser().getUsername()))
                .toList();

        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getCreatedBy().getUsername(),
                members
        );
    }
}
