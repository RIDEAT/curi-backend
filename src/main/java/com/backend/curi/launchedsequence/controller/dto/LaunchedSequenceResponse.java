package com.backend.curi.launchedsequence.controller.dto;

import com.backend.curi.launchedsequence.repository.entity.LaunchedSequence;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LaunchedSequenceResponse {

    private Long id;

    private String name;

    private String status;

    private String applyDate;

    private Long order;

    /*
    private Long employeeId;

    private Long workflowId;

    private Long nextSequenceId;

    private Long workspaceId;
*/

    public static LaunchedSequenceResponse of (LaunchedSequence launchedSequence){
        return new LaunchedSequenceResponse(
                launchedSequence.getId(),
                launchedSequence.getName(),
                launchedSequence.getStatus().toString(),
                launchedSequence.getApplyDate().toString(),
                launchedSequence.getOrderInWorkflow()
                /*
                launchedWorkflow.getEmployee().getId(),
                launchedWorkflow.getWorkflow().getId(),
                launchedWorkflow.getWorkspace().getId()*/
        );



    }
}
