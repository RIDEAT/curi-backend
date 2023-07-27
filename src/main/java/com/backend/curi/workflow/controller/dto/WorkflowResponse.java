package com.backend.curi.workflow.controller.dto;

import com.backend.curi.workflow.repository.entity.Workflow;
import com.backend.curi.workflow.repository.entity.WorkflowSequence;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private List<SequenceResponse> sequences;
    public static WorkflowResponse of(Workflow workflow) {
        var sequences =
                workflow.getWorkflowSequences().stream()
                        .map(WorkflowSequence::getSequence)
                        .map(SequenceResponse::of).collect(Collectors.toList());
        return new WorkflowResponse(
                workflow.getId(),
                workflow.getName(),
                sequences);
    }
    public static WorkflowResponse listOf(Workflow workflow) {
        return new WorkflowResponse(
                workflow.getId(),
                workflow.getName(),
                new ArrayList<>());
    }
}

