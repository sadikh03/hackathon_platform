package com.hackathon.hackathon_platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TeamRequest {

    @NotBlank(message = "Le nom de l'équipe est obligatoire")
    @Size(min = 3, max = 50)
    private String name;
}
