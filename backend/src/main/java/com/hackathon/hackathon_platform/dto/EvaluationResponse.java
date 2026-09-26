package com.hackathon.hackathon_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EvaluationResponse {
    private Long id;
    private Integer innovationScore;
    private Integer technicalScore;
    private Integer presentationScore;
    private double totalScore;
    private String comment;
    private Long projectId;
    private String projectTitle;
    private String juryUsername;
}