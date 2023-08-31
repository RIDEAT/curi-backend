package com.backend.curi.launched.controller.dto;

import com.backend.curi.launched.repository.entity.LaunchedStatus;
import com.backend.curi.launched.repository.entity.LaunchedWorkflow;

import com.backend.curi.member.controller.dto.EmployeeManagerDetail;
import com.backend.curi.member.controller.dto.MemberResponse;
import com.backend.curi.workspace.controller.dto.WorkspaceResponse;
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

    private List<LaunchedSequenceResponse> launchedSequences;

    private List<EmployeeManagerDetail> roleDetails;

    private MemberResponse employee;

    private WorkspaceResponse workspaceResponse;


    public static LaunchedWorkflowResponse of (LaunchedWorkflow launchedWorkflow){
        return new LaunchedWorkflowResponse(
                launchedWorkflow.getId(),
                launchedWorkflow.getName(),
                launchedWorkflow.getStatus(),
                launchedWorkflow.getKeyDate(),
                launchedWorkflow.getLaunchedSequences().stream()
                        .map(LaunchedSequenceResponse::of)
                        .sorted(Comparator.comparing(LaunchedSequenceResponse::getApplyDate))
                        .collect(Collectors.toList()),
                launchedWorkflow.getLaunchedWorkflowManagers().stream()
                        .map(EmployeeManagerDetail::of)
                        .collect(Collectors.toList()),
                MemberResponse.of(launchedWorkflow.getMember()), WorkspaceResponse.of(launchedWorkflow.getWorkspace())


                /*
                launchedWorkflow.getEmployee().getId(),
                launchedWorkflow.getWorkflow().getId(),
                launchedWorkflow.getWorkspace().getId()*/
        );



    }
}
