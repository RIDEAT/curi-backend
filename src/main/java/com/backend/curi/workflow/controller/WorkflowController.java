package com.backend.curi.workflow.controller;

import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.workflow.controller.dto.WorkflowForm;
import com.backend.curi.workflow.controller.dto.WorkflowListResponse;
import com.backend.curi.workflow.controller.dto.WorkflowRequest;
import com.backend.curi.workflow.controller.dto.WorkflowResponse;
import com.backend.curi.workflow.service.WorkflowService;
import com.backend.curi.workspace.controller.dto.WorkspaceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping("/workflow/{workspaceId}")
public class WorkflowController {
    private final WorkflowService workflowService;

    @PostMapping("/create")
    public ResponseEntity<WorkflowResponse> createWorkflow(@RequestBody @Valid WorkflowRequest request,
                                                           @PathVariable Long workspaceId,
                                                           Authentication authentication){
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        var response = workflowService.createWorkflow(currentUser, workspaceId,request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<WorkflowListResponse> getWorkflowList (@PathVariable Long workspaceId, Authentication authentication){
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        var response = workflowService.getWorkflows(currentUser, workspaceId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<WorkflowResponse> setWorkflow(@RequestBody @Valid WorkflowRequest request,
                                      @PathVariable Long workspaceId,
                                      Authentication authentication){
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        var response = workflowService.updateWorkflow(currentUser, workspaceId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<WorkflowResponse> deleteWorkflow(@RequestBody @Valid WorkflowRequest request,
                                      @PathVariable Long workspaceId,
                                      Authentication authentication){
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        var response = workflowService.deleteWorkflow(currentUser, workspaceId, request);
        return ResponseEntity.ok(response);
    }
}
