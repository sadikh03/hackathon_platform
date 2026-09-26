package com.hackathon.hackathon_platform.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EvaluationRequest {

    @NotNull(message = "Le score d'innovation est obligatoire")
    @Min(0) @Max(10)
    private Integer innovationScore;

    @NotNull(message = "Le score technique est obligatoire")
    @Min(0) @Max(10)
    private Integer technicalScore;

    @NotNull(message = "Le score de présentation est obligatoire")
    @Min(0) @Max(10)
    private Integer presentationScore;

    private String comment; // optionnel
}
