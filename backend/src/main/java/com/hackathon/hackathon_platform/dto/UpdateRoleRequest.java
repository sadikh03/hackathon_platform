package com.hackathon.hackathon_platform.dto;

import com.hackathon.hackathon_platform.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoleRequest {

    @NotNull(message = "Le rôle est obligatoire")
    private Role role;
}
