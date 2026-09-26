package com.hackathon.hackathon_platform.repository;

import com.hackathon.hackathon_platform.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByTeamId(Long teamId);

    boolean existsByTeamId(Long teamId);
}
