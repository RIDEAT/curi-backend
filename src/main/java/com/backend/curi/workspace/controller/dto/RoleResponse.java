package com.backend.curi.workspace.controller.dto;

import com.backend.curi.workspace.repository.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponse {
    private Long id;
    private String name;

    public static RoleResponse of(Role role) {
        return new RoleResponse(
                role.getId(),
                role.getName());
    }
}
