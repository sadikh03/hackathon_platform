package com.hackathon.hackathon_platform.repository;
import com.hackathon.hackathon_platform.entity.Team;
import com.hackathon.hackathon_platform.entity.TeamMember;
import com.hackathon.hackathon_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findByTeamId(Long teamId);

    Optional<TeamMember> findByUserAndTeam(User user, Team team);

    Optional<TeamMember> findByUserId(Long userId);

    boolean existsByUserAndTeam(User user, Team team);

    void deleteByUserAndTeam(User user, Team team);
}
