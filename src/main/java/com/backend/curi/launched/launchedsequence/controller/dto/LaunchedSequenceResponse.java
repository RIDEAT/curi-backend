package com.backend.curi.launched.launchedsequence.controller.dto;

import com.backend.curi.launched.launchedmodule.controller.dto.LaunchedModuleResponse;
import com.backend.curi.launched.launchedsequence.repository.entity.LaunchedSequence;
import com.backend.curi.launched.launchedworkflow.repository.entity.LaunchedStatus;
import com.backend.curi.member.controller.dto.MemberResponse;
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
public class LaunchedSequenceResponse {

    private Long id;

    private String name;

    private LaunchedStatus status;

    private LocalDate applyDate;

    private MemberResponse assignedMember;

    private List<LaunchedModuleResponse> launchedModuleResponseList;
/*
    private Long workflowId;

    private Long nextSequenceId;

    private Long workspaceId;
*/

    public static LaunchedSequenceResponse of (LaunchedSequence launchedSequence){
        return new LaunchedSequenceResponse(
                launchedSequence.getId(),
                launchedSequence.getName(),
                launchedSequence.getStatus(),
                launchedSequence.getApplyDate(),
                MemberResponse.of(launchedSequence.getMember()),
                launchedSequence.getLaunchedModules() != null
                        ? launchedSequence.getLaunchedModules().stream()
                        .map(LaunchedModuleResponse::of)
                        .sorted(Comparator.comparing(LaunchedModuleResponse::getOrder))
                        .collect(Collectors.toList())
                        : Collections.emptyList()
               /* launchedSequence.getOrderInWorkflow()

                launchedWorkflow.getEmployee().getId(),
                launchedWorkflow.getWorkflow().getId(),
                launchedWorkflow.getWorkspace().getId()*/
        );



    }
}
