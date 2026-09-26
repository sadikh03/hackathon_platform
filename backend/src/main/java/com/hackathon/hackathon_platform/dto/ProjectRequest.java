package com.hackathon.hackathon_platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjectRequest {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, max = 100)
    private String title;

    @NotBlank(message = "La description est obligatoire")
    @Size(max = 2000)
    private String description;

    // Pas de @NotBlank : le cahier des charges précise "optionnel"
    private String githubLink;
}