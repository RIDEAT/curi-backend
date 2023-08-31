package com.backend.curi.launched.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LaunchedWorkflowsResponse {
    private String name;
    private List<LaunchedSequenceResponse> launchedSequenceResponses;
    private List<LaunchedEmployeeResponse> employees;


    public static LaunchedWorkflowsResponse of(List<LaunchedWorkflowResponse> responses){
        var workflowName = responses.get(0).getName();
        var commonSequences = responses.get(0).getLaunchedSequences();
        var employees = responses.stream().map(LaunchedEmployeeResponse::of).collect(Collectors.toList());
        return new LaunchedWorkflowsResponse(
                workflowName,
                commonSequences,
                employees
        );
    }
}
