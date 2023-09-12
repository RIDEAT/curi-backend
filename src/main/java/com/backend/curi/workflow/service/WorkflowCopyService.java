package com.backend.curi.workflow.service;


import com.backend.curi.workflow.repository.entity.Workflow;
import com.backend.curi.workspace.repository.entity.Workspace;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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
    @Transactional
    public void copyWorkflow(Workspace workspace, Long workflowId){
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
    }
}
