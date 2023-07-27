package com.backend.curi.launched.launchedworkflow.controller.dto;

import com.backend.curi.launched.launchedworkflow.repository.entity.LaunchedWorkflow;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LaunchedWorkflowResponse {

    private Long id;

    private String name;

    private String status;

    private String keyDate;
    /*
    private Long employeeId;

    private Long workflowId;

    private Long workspaceId;
*/

    public static LaunchedWorkflowResponse of (LaunchedWorkflow launchedWorkflow){
        return new LaunchedWorkflowResponse(
                launchedWorkflow.getId(),
                launchedWorkflow.getName(),
                launchedWorkflow.getStatus().toString(),
                launchedWorkflow.getKeyDate().toString()
                /*
                launchedWorkflow.getEmployee().getId(),
                launchedWorkflow.getWorkflow().getId(),
                launchedWorkflow.getWorkspace().getId()*/
        );



    }
}
