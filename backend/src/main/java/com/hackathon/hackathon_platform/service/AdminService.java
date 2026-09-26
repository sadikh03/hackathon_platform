package com.hackathon.hackathon_platform.service;

import com.hackathon.hackathon_platform.dto.*;
import com.hackathon.hackathon_platform.entity.*;
import com.hackathon.hackathon_platform.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;

    public AdminService(UserRepository userRepository,
                        TeamRepository teamRepository,
                        TeamMemberRepository teamMemberRepository,
                        ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.projectRepository = projectRepository;
    }

    // ---- Utilisateurs ----

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(u -> new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole().name()))
                .toList();
    }

    @Transactional
    public UserResponse updateUserRole(Long userId, UpdateRoleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        user.setRole(request.getRole());
        userRepository.save(user);

        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name());
    }

    @Transactional
    public void deleteUser(Long userId, User currentAdmin) {
        if (userId.equals(currentAdmin.getId())) {
            throw new IllegalStateException("Vous ne pouvez pas supprimer votre propre compte");
        }
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("Utilisateur introuvable");
        }
        userRepository.deleteById(userId);
    }

    // ---- Équipes ----

    @Transactional
    public void deleteTeam(Long teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new IllegalArgumentException("Équipe introuvable");
        }
        // Le cascade défini sur Team (TeamMember + Project) gère la suppression en chaîne
        teamRepository.deleteById(teamId);
    }

    // ---- Projets ----

    @Transactional
    public void deleteProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Projet introuvable");
        }
        projectRepository.deleteById(projectId);
    }
}