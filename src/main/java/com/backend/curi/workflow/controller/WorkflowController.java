package com.backend.curi.workflow.controller;

import com.backend.curi.exception.sequence.ValidationSequence;
import com.backend.curi.launched.controller.dto.LaunchedWorkflowResponse;
import com.backend.curi.launched.repository.entity.LaunchedWorkflow;
import com.backend.curi.launched.service.LaunchedWorkflowService;
import com.backend.curi.workflow.controller.dto.*;
import com.backend.curi.workflow.service.LaunchService;
import com.backend.curi.workflow.service.WorkflowService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/workspaces/{workspaceId}/workflows")
public class WorkflowController {
    private final WorkflowService workflowService;
    private final LaunchedWorkflowService launchedWorkflowService;
    private final LaunchService launchService;

    @GetMapping("/{workflowId}/requiredforlaunch")
    public ResponseEntity<RequiredForLaunchResponse> getRequiredForLaunch(@PathVariable Long workspaceId, @PathVariable Long workflowId){
        var requiredForLaunchResponse = launchService.getRequiredForLaunch(workflowId);
        return ResponseEntity.ok(requiredForLaunchResponse);
    }


    @PostMapping("/{workflowId}/launch")
    public ResponseEntity<LaunchedWorkflowResponse> launchWorkflow(@RequestBody @Validated(ValidationSequence.class) LaunchRequest launchRequest, @PathVariable Long workspaceId, @PathVariable Long workflowId) throws JsonProcessingException {
        var launchResponse = launchService.launchWorkflow(workflowId, launchRequest, workspaceId);
        return ResponseEntity.status(HttpStatus.CREATED).body(launchResponse);
    }

    @PostMapping
    public ResponseEntity<WorkflowResponse> createWorkflow(@RequestBody @Validated(ValidationSequence.class) WorkflowRequest request,
                                               @PathVariable Long workspaceId,
                                               Authentication authentication) {
        var response = workflowService.createWorkflow(workspaceId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping
    public ResponseEntity<List<WorkflowResponse>> getWorkflows(@PathVariable Long workspaceId) {
        var response = workflowService.getWorkflows(workspaceId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{workflowId}")
    public ResponseEntity<WorkflowResponse> getWorkflow(@PathVariable Long workspaceId, @PathVariable Long workflowId) {
        var response = workflowService.getWorkflow(workflowId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{workflowId}/sequences")
    public ResponseEntity<List<SequenceResponse>> getSequences(@PathVariable Long workflowId) {
        var response = workflowService.getSequences(workflowId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{workflowId}")
    public ResponseEntity<WorkflowResponse> updateWorkflow(@RequestBody @Validated(ValidationSequence.class) WorkflowRequest request,
                                               @PathVariable Long workflowId,
                                               Authentication authentication) {
        var response = workflowService.updateWorkflow(workflowId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{workflowId}")
    public ResponseEntity<WorkflowResponse> deleteWorkflow(@PathVariable Long workflowId,
                                               Authentication authentication) {
        workflowService.deleteWorkflow(workflowId);
        var response = new WorkflowResponse();
        response.setCreatedDate(LocalDateTime.now());
        response.setUpdatedDate(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
