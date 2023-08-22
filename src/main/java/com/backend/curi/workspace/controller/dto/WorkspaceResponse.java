package com.backend.curi.workspace.controller.dto;

import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkspaceResponse {
    private Long id;
    private String name;
    private String email;
    private String logoUrl;
    private List<RoleResponse> roles;
    public static WorkspaceResponse of(Workspace workspace){
        var responseList = workspace.getRoles().stream().map(RoleResponse::of).collect(Collectors.toList());
        return new WorkspaceResponse(
                workspace.getId(),
                workspace.getName(),
                workspace.getEmail(),
                workspace.getLogoUrl(),
                responseList);
    }
}
