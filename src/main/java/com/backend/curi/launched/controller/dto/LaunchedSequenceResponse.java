package com.backend.curi.launched.controller.dto;

import com.backend.curi.frontoffice.controller.dto.SequenceSatisfactionResponse;
import com.backend.curi.launched.repository.entity.LaunchedSequence;
import com.backend.curi.launched.repository.entity.LaunchedStatus;
import com.backend.curi.member.controller.dto.MemberResponse;
import com.backend.curi.workspace.controller.dto.RoleResponse;
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
public class LaunchedSequenceResponse {

    private Long id;

    private String name;

    private Boolean checkSatisfaction;

    private SequenceSatisfactionResponse sequenceSatisfactionResponse;

    private LaunchedStatus status;

    private LocalDate applyDate;

    private MemberResponse assignedMember;

    private RoleResponse roleResponse;

    private WorkspaceResponse workspaceResponse;

    private List<LaunchedModuleResponse> launchedModules;

    public static LaunchedSequenceResponse of (LaunchedSequence launchedSequence){
        return new LaunchedSequenceResponse(
                launchedSequence.getId(),
                launchedSequence.getName(),
                launchedSequence.getCheckSatisfaction(),
                SequenceSatisfactionResponse.of(launchedSequence.getSequenceSatisfaction()),
                launchedSequence.getStatus(),
                launchedSequence.getApplyDate(),
                MemberResponse.of(launchedSequence.getMember()),
                RoleResponse.of(launchedSequence.getRole()),
                WorkspaceResponse.of(launchedSequence.getWorkspace()),
               launchedSequence.getLaunchedModules().stream()
                        .map(LaunchedModuleResponse::of)
                        .sorted(Comparator.comparing(LaunchedModuleResponse::getOrder))
                        .collect(Collectors.toList())
               /* launchedSequence.getOrderInWorkflow()

                launchedWorkflow.getEmployee().getId(),
                launchedWorkflow.getWorkflow().getId(),
                launchedWorkflow.getWorkspace().getId()*/
        );



    }
}
