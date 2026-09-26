package com.hackathon.hackathon_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DeadlineResponse {
    private LocalDateTime submissionDeadline;
}