package com.backend.curi.workflow.service;

import com.backend.curi.workflow.controller.dto.WorkflowForm;
import com.backend.curi.workflow.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WorkflowService {
    private final WorkflowRepository workflowRepository;

    public int createWorkflow (WorkflowForm workflowForm){
        return 0;
    }

}
