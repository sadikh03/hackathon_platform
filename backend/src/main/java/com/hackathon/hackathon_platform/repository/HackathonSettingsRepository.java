package com.hackathon.hackathon_platform.repository;

import com.hackathon.hackathon_platform.entity.HackathonSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HackathonSettingsRepository extends JpaRepository<HackathonSettings, Long> {
}
