package com.hackathon.hackathon_platform.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DeadlineRequest {

    @NotNull(message = "La deadline est obligatoire")
    private LocalDateTime submissionDeadline;
}
