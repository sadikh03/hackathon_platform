package com.hackathon.hackathon_platform.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "projects")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private String githubLink;

    @OneToOne
    @JoinColumn(name = "team_id", nullable = false, unique = true)
    private Team team;

    @Column(name = "file_name")
    private String fileName; // nom original du fichier, ex: "pitch.pdf"

    @Column(name = "file_path")
    private String filePath; // chemin de stockage réel sur le serveur
}
