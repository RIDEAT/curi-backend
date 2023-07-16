package com.backend.curi.workflow.controller.dto;

import com.backend.curi.workflow.repository.entity.Workflow;
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
public class WorkflowListResponse {
    private String status;
    private List<WorkflowResponse> workflowList;

    public static WorkflowListResponse of(List<Workflow> workflowList) {
        var responseList = workflowList.stream()
                .map(WorkflowResponse::of)
                .collect(Collectors.toList());
        return new WorkflowListResponse("success", responseList);
    }
}
