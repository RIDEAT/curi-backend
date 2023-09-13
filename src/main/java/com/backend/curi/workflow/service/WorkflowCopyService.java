package com.backend.curi.workflow.service;


import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.workflow.controller.dto.WorkflowResponse;
import com.backend.curi.workflow.repository.entity.Workflow;
import com.backend.curi.workspace.repository.entity.Workspace;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkflowCopyService {
    private final WorkflowService workflowService;
    private final SequenceService sequenceService;
    private final ModuleService moduleService;

    @Value("#{'${workplug.template.array}'.split(',')}")
    private String[] templateWorkflows;

    public void copyTemplateWorkflows(Workspace workspace){
        if(templateWorkflows[0].equals("None")){
            return;
        }
        for (var templateWorkflowId : templateWorkflows) {
            copyWorkflow(workspace, Long.parseLong(templateWorkflowId));
        }
    }

    public List<WorkflowResponse> getTemplateWorkflows(){
        if(templateWorkflows[0].equals("None")){
            return new ArrayList<WorkflowResponse>();
        }
        var workflows = new ArrayList<WorkflowResponse>();
        for (var templateWorkflowId : templateWorkflows) {
            workflows.add(WorkflowResponse.of(workflowService.getWorkflowEntity(Long.parseLong(templateWorkflowId))));
        }
        return workflows;
    }
    @Transactional
    public WorkflowResponse copyWorkflow(Workspace workspace, Long workflowId){
        if(!isValidTemplateId(workflowId)){
            throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.INVALID_REQUEST_ERROR);
        }
        var workflowOrigin = workflowService.getWorkflowEntity(workflowId);
        var workflowCopy = workflowService.copyWorkflow(workspace, workflowOrigin);

        var sequenceList = workflowOrigin.getSequences();
        for (var sequence : sequenceList) {
            var sequenceCopy = sequenceService.copySequence(workspace, workflowCopy, sequence);
            var moduleList = sequence.getModules();
            for (var module : moduleList) {
                moduleService.copyModule(workspace, sequenceCopy, module);
            }
        }
        return WorkflowResponse.of(workflowCopy);
    }

    private Boolean isValidTemplateId(Long templateId){
        if(templateWorkflows[0].equals("None")){
            return false;
        }
        for (var templateWorkflowId : templateWorkflows) {
            if(templateId.equals(Long.parseLong(templateWorkflowId))){
                return true;
            }
        }
        return false;
    }
}
