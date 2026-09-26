package com.hackathon.hackathon_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectResponse {
    private Long id;
    private String title;
    private String description;
    private String githubLink;
    private Long teamId;
    private String teamName;
    private String fileName; // null si pas de fichier attaché
}
