package com.backend.curi.workFlow.service;

import com.backend.curi.workFlow.controller.dto.WorkFlowForm;
import com.backend.curi.workFlow.repository.WorkFlowRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WorkFlowService {
    private final WorkFlowRepository workFlowRepository;

    public int createWorkFlow (WorkFlowForm workFlowForm){
        return 0;
    }

}
