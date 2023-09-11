package com.backend.curi.workflow.service;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.slack.controller.dto.SlackMessageRequest;
import com.backend.curi.slack.service.SlackService;
import com.backend.curi.workflow.controller.dto.SequenceResponse;
import com.backend.curi.workflow.controller.dto.WorkflowRequest;
import com.backend.curi.workflow.controller.dto.WorkflowResponse;
import com.backend.curi.workflow.repository.WorkflowRepository;
import com.backend.curi.workflow.repository.entity.Sequence;
import com.backend.curi.workflow.repository.entity.Workflow;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class WorkflowService {
    private final WorkflowRepository workflowRepository;
    private final WorkspaceService workspaceService;
    private final SlackService slackService;
    private static Logger log = LoggerFactory.getLogger(WorkflowService.class);

    @Transactional
    public WorkflowResponse createWorkflow (Long workspaceId, WorkflowRequest request){
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);

        var workflow = Workflow.builder()
                .name(request.getName())
                .workspace(workspace)
                .build();
        workflowRepository.save(workflow);
        slackService.sendMessageToRideat(new SlackMessageRequest("새로운 워크플로우가 생성되었습니다. 이름 : " + request.getName() + ", 워크스페이스 : " + workspace.getId()));

        return WorkflowResponse.of(workflow);
    }

    public WorkflowResponse getWorkflow(Long workflowId){
        var workflow = getWorkflowEntity(workflowId);

        return WorkflowResponse.of(workflow);
    }

    @Transactional
    public List<WorkflowResponse> getWorkflows(Long workspaceId){
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        var workflowList = workflowRepository.findAllByWorkspace(workspace);
        return workflowList.stream().map(WorkflowResponse::of).collect(Collectors.toList());
    }

    @Transactional
    public WorkflowResponse updateWorkflow(Long workflowId, WorkflowRequest request){
        var workflow = getWorkflowEntity(workflowId);
        workflow.modify(request);
        return WorkflowResponse.of(workflow);
    }

    public void deleteWorkflow (Long workflowId){
        var workflow = getWorkflowEntity(workflowId);
        workflowRepository.delete(workflow);
    }

    public List<SequenceResponse> getSequences(Long workflowId){
        var workflow = getWorkflowEntity(workflowId);
        var sequenceList = workflow.getSequences();
        sequenceList.sort((o1, o2) -> o1.getDayOffset().compareTo(o2.getDayOffset()));
        return sequenceList.stream().map(SequenceResponse::of).collect(Collectors.toList());
    }

    public Workflow getWorkflowEntity(Long workflowId){
        return workflowRepository.findById(workflowId)
                .orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKFLOW_NOT_EXISTS));
    }





}
