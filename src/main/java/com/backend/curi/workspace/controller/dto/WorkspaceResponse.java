package com.backend.curi.workspace.controller.dto;

import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkspaceResponse {
    private Long id;
    private String name;
    private String email;

    public static WorkspaceResponse of(Workspace workspace){
        return new WorkspaceResponse(
                workspace.getId(),
                workspace.getName(),
                workspace.getEmail());
    }
}
