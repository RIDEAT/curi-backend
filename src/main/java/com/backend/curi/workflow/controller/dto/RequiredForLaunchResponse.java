package com.backend.curi.workflow.controller.dto;

import com.backend.curi.workspace.controller.dto.RoleResponse;
import com.backend.curi.workspace.repository.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequiredForLaunchResponse {
    List<RoleResponse> requiredRoles;
    public RequiredForLaunchResponse of (List<Role> roles){
        return new RequiredForLaunchResponse(roles.stream().map(RoleResponse::of).collect(Collectors.toList()));
    }
}
