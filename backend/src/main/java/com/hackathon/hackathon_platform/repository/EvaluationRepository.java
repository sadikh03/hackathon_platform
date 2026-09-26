package com.hackathon.hackathon_platform.repository;

import com.hackathon.hackathon_platform.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    List<Evaluation> findByProjectId(Long projectId);

    List<Evaluation> findByJuryId(Long juryId);

    Optional<Evaluation> findByProjectIdAndJuryId(Long projectId, Long juryId);

    boolean existsByProjectIdAndJuryId(Long projectId, Long juryId);
}
