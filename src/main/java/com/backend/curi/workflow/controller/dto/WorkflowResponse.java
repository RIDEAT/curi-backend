package com.backend.curi.workflow.controller.dto;

import com.backend.curi.workflow.repository.entity.Workflow;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WorkflowResponse {
    private Long id;
    private String name;

    public static WorkflowResponse of(Workflow workflow) {
        return new WorkflowResponse(
                workflow.getId(),
                workflow.getName());
    }
}

