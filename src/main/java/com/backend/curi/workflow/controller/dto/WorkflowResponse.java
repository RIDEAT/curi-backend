package com.backend.curi.workflow.controller.dto;

import com.backend.curi.workflow.repository.entity.Workflow;
import com.backend.curi.workspace.controller.dto.RoleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WorkflowResponse {
    private Long id;
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    private List<SequenceResponse> sequences;

    private List<RoleResponse> requiredRoles;
    public static WorkflowResponse of(Workflow workflow) {
        var sequences =
                workflow.getSequences().stream()
                        .map(SequenceResponse::of).collect(Collectors.toList());

        var roles = sequences.stream()
                .map(SequenceResponse::getRole)
                .distinct()  // 중복 요소 제거
                .collect(Collectors.toList());

        return new WorkflowResponse(
                workflow.getId(),
                workflow.getName(),
                workflow.getCreatedDate(),
                workflow.getUpdatedDate(),
                sequences,roles
               );
    }
    public static WorkflowResponse listOf(Workflow workflow) {
        return new WorkflowResponse(
                workflow.getId(),
                workflow.getName(),
                workflow.getCreatedDate(),
                workflow.getUpdatedDate(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }
}

