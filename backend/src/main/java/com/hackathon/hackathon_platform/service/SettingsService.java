package com.hackathon.hackathon_platform.service;

import com.hackathon.hackathon_platform.dto.*;
import com.hackathon.hackathon_platform.entity.HackathonSettings;
import com.hackathon.hackathon_platform.repository.HackathonSettingsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SettingsService {

    private static final Long SETTINGS_ID = 1L;

    private final HackathonSettingsRepository settingsRepository;

    public SettingsService(HackathonSettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public DeadlineResponse getDeadline() {
        LocalDateTime deadline = settingsRepository.findById(SETTINGS_ID)
                .map(HackathonSettings::getSubmissionDeadline)
                .orElse(null); // pas encore configurée

        return new DeadlineResponse(deadline);
    }

    public DeadlineResponse setDeadline(DeadlineRequest request) {

        HackathonSettings settings = settingsRepository.findById(SETTINGS_ID)
                .orElse(HackathonSettings.builder().id(SETTINGS_ID).build());

        settings.setSubmissionDeadline(request.getSubmissionDeadline());
        settingsRepository.save(settings);

        return new DeadlineResponse(settings.getSubmissionDeadline());
    }

    // Méthode utilitaire réutilisée par ProjectService
    public void checkDeadlineNotPassed() {
        LocalDateTime deadline = settingsRepository.findById(SETTINGS_ID)
                .map(HackathonSettings::getSubmissionDeadline)
                .orElse(null);

        if (deadline != null && LocalDateTime.now().isAfter(deadline)) {
            throw new IllegalStateException("La deadline de soumission est dépassée (" + deadline + ")");
        }
    }
}
