package com.hackathon.hackathon_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class TeamResponse {
    private Long id;
    private String name;
    private String createdBy;
    private List<TeamMemberResponse> members;
}
