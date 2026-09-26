package com.hackathon.hackathon_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaderboardEntryResponse {
    private int rank;
    private Long teamId;
    private String teamName;
    private Long projectId;
    private String projectTitle;
    private double averageScore;
    private int evaluationsCount;
}
