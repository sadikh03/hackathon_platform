package com.hackathon.hackathon_platform.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "evaluations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"project_id", "jury_id"})
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer innovationScore;   // /10

    @Column(nullable = false)
    private Integer technicalScore;    // /10

    @Column(nullable = false)
    private Integer presentationScore; // /10

    @Column(length = 1000)
    private String comment;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "jury_id", nullable = false)
    private User jury;

    @Transient
    public double getTotalScore() {
        return (innovationScore + technicalScore + presentationScore) / 3.0;
    }
}
