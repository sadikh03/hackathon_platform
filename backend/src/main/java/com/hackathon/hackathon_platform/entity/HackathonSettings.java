package com.hackathon.hackathon_platform.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hackathon_settings")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class HackathonSettings {

    @Id
    private Long id; // toujours = 1L, table à une seule ligne (singleton)

    private LocalDateTime submissionDeadline; // null = pas de deadline définie
}
