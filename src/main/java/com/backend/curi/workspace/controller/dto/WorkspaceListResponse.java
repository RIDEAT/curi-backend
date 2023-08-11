package com.backend.curi.workspace.controller.dto;


import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WorkspaceListResponse {
    List<WorkspaceResponse> workspaceList;

    public static WorkspaceListResponse of(List<Workspace> workspaceList){
        var responseList = workspaceList.stream()
                .map(WorkspaceResponse::of)
                .collect(Collectors.toList());
        return new WorkspaceListResponse(responseList);
    }
}
