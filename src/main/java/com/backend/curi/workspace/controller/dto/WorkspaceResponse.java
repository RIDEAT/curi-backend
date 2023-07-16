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
    private String status;
    private String name;
    private String email;

    public static WorkspaceResponse ofSuccess(Workspace workspace){
        return new WorkspaceResponse("success",
                workspace.getName(),
                workspace.getEmail());
    }
}
