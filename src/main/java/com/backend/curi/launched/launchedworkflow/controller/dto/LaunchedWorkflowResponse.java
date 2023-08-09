package com.backend.curi.launched.launchedworkflow.controller.dto;

import com.backend.curi.launched.launchedsequence.controller.dto.LaunchedSequenceResponse;
import com.backend.curi.launched.launchedworkflow.repository.entity.LaunchedStatus;
import com.backend.curi.launched.launchedworkflow.repository.entity.LaunchedWorkflow;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LaunchedWorkflowResponse {

    private Long id;

    private String name;

    private LaunchedStatus status;

    private LocalDate keyDate;

    private List<LaunchedSequenceResponse> launchedSequenceList;

    /*
    private Long employeeId;

    private Long workflowId;

    private Long workspaceId;
*/

    public static LaunchedWorkflowResponse of (LaunchedWorkflow launchedWorkflow){
        return new LaunchedWorkflowResponse(
                launchedWorkflow.getId(),
                launchedWorkflow.getName(),
                launchedWorkflow.getStatus(),
                launchedWorkflow.getKeyDate(),
                launchedWorkflow.getLaunchedSequences() != null
                        ? launchedWorkflow.getLaunchedSequences().stream()
                        .map(LaunchedSequenceResponse::of)
                        .sorted(Comparator.comparing(LaunchedSequenceResponse::getApplyDate))
                        .collect(Collectors.toList())
                        : Collections.emptyList()

                /*
                launchedWorkflow.getEmployee().getId(),
                launchedWorkflow.getWorkflow().getId(),
                launchedWorkflow.getWorkspace().getId()*/
        );



    }
}
