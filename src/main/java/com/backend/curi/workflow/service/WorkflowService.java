package com.backend.curi.workflow.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.service.UserService;
import com.backend.curi.userworkspace.service.UserworkspaceService;
import com.backend.curi.workflow.controller.dto.WorkflowListResponse;
import com.backend.curi.workflow.controller.dto.WorkflowRequest;
import com.backend.curi.workflow.controller.dto.WorkflowResponse;
import com.backend.curi.workflow.repository.WorkflowRepository;
import com.backend.curi.workflow.repository.entity.Workflow;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkflowService {
    private final WorkflowRepository workflowRepository;
    private final UserService userService;
    private final WorkspaceService workspaceService;
    private final UserworkspaceService userworkspaceService;
    public WorkflowResponse createWorkflow (CurrentUser currentUser, Long workspaceId, WorkflowRequest request){
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        userworkspaceService.checkAuthentication(currentUser, workspace);

        var workflow = Workflow.builder()
                .name(request.getName())
                .workspace(workspace)
                .build();

        workflowRepository.save(workflow);

        return WorkflowResponse.of(workflow);
    }

    public WorkflowResponse getWorkflow(CurrentUser currentUser, Long workspaceId, WorkflowRequest request){
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        userworkspaceService.checkAuthentication(currentUser, workspace);

        var workflow = workflowRepository.findById(request.getId()).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKFLOW_NOT_EXISTS));

        return WorkflowResponse.of(workflow);
    }

    public WorkflowListResponse getWorkflows(CurrentUser currentUser, Long workspaceId){
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        userworkspaceService.checkAuthentication(currentUser, workspace);

        var workflowList = workflowRepository.findAllByWorkspace(workspace);

        return WorkflowListResponse.of(workflowList);
    }

    @Transactional
    public WorkflowResponse updateWorkflow(CurrentUser currentUser, Long workspaceId, WorkflowRequest request){
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        userworkspaceService.checkAuthentication(currentUser, workspace);

        var workflow = workflowRepository.findById(request.getId()).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKFLOW_NOT_EXISTS));

        //some modify code

        return WorkflowResponse.of(workflow);
    }

    public WorkflowResponse deleteWorkflow (CurrentUser currentUser, Long workspaceId, WorkflowRequest request){
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        userworkspaceService.checkAuthentication(currentUser, workspace);

        var workflow = workflowRepository.findById(request.getId()).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKFLOW_NOT_EXISTS));
        workflowRepository.delete(workflow);

        return WorkflowResponse.of(workflow);
    }

    public Workflow getWorkflowById(Long workflowId){
        Optional<Workflow> workflowOptional = workflowRepository.findById(workflowId);
        return workflowOptional.orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKFLOW_NOT_EXISTS));
    }

}
