package com.hackathon.hackathon_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamMemberResponse {
    private Long userId;
    private String username;
}
