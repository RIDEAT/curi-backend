package com.backend.curi.workflow.service;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.launched.launchedworkflow.repository.entity.LaunchedWorkflow;
import com.backend.curi.launched.launchedworkflow.service.LaunchedWorkflowService;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.service.UserService;
import com.backend.curi.userworkspace.service.UserworkspaceService;
import com.backend.curi.workflow.controller.dto.SequenceResponse;
import com.backend.curi.workflow.controller.dto.WorkflowRequest;
import com.backend.curi.workflow.controller.dto.WorkflowResponse;
import com.backend.curi.workflow.repository.WorkflowRepository;
import com.backend.curi.workflow.repository.entity.Sequence;
import com.backend.curi.workflow.repository.entity.Workflow;
import com.backend.curi.workflow.repository.entity.WorkflowSequence;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkflowService {
    private final WorkflowRepository workflowRepository;
    private final WorkspaceService workspaceService;





    @Transactional
    public WorkflowResponse createWorkflow (Long workspaceId, WorkflowRequest request){
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);

        var workflow = Workflow.builder()
                .name(request.getName())
                .workspace(workspace)
                .build();
        workflowRepository.save(workflow);

        return WorkflowResponse.of(workflow);
    }

    public WorkflowResponse getWorkflow(Long workflowId){
        var workflow = getWorkflowEntity(workflowId);

        return WorkflowResponse.of(workflow);
    }

    public List<WorkflowResponse> getWorkflows(Long workspaceId){
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        var workflowList = workflowRepository.findAllByWorkspace(workspace);
        var responseList = workflowList.stream().map(WorkflowResponse::listOf).collect(Collectors.toList());
        return responseList;
    }

    @Transactional
    public void updateWorkflow(Long workflowId, WorkflowRequest request){
        var workflow = getWorkflowEntity(workflowId);
        workflow.modify(request);
    }

    public void deleteWorkflow (Long workflowId){
        var workflow = getWorkflowEntity(workflowId);
        workflowRepository.delete(workflow);
    }

    public List<SequenceResponse> getSequences(Long workflowId){
        var workflow = getWorkflowEntity(workflowId);
        var sequenceList = workflow.getWorkflowSequences();
        sequenceList.sort((o1, o2) -> o1.getDayOffset().compareTo(o2.getDayOffset()));
        var responseList = sequenceList.stream().map(WorkflowSequence::getSequence).map(SequenceResponse::of).collect(Collectors.toList());
        return responseList;
    }

    public List<SimpleEntry<Sequence, Integer>> getSequencesWithDayoffset(Long workflowId){
        Workflow workflow = getWorkflowEntity(workflowId);
        List<WorkflowSequence> workflowSequences = workflow.getWorkflowSequences();
        workflowSequences.sort((o1, o2) -> o1.getDayOffset().compareTo(o2.getDayOffset()));

        List<SimpleEntry<Sequence, Integer>> responseWithDayoffsetList = new ArrayList<>();
        for (var workflowSequence : workflowSequences) {
            var sequence = workflowSequence.getSequence();
            Integer dayOffset = workflowSequence.getDayOffset();
            responseWithDayoffsetList.add(new SimpleEntry<>(sequence, dayOffset));
        }
        return responseWithDayoffsetList;
    }

    public Workflow getWorkflowEntity(Long workflowId){
        return workflowRepository.findById(workflowId)
                .orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKFLOW_NOT_EXISTS));
    }

    public Workflow getWorkflowById(Long workflowId){
        Optional<Workflow> workflowOptional = workflowRepository.findById(workflowId);
        return workflowOptional.orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKFLOW_NOT_EXISTS));
    }

}
