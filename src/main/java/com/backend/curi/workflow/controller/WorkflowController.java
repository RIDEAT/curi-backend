package com.backend.curi.workflow.controller;

import com.backend.curi.exception.sequence.ValidationSequence;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.workflow.controller.dto.LaunchRequest;
import com.backend.curi.workflow.controller.dto.SequenceResponse;
import com.backend.curi.workflow.controller.dto.WorkflowRequest;
import com.backend.curi.workflow.controller.dto.WorkflowResponse;
import com.backend.curi.workflow.service.LaunchService;
import com.backend.curi.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/workspaces/{workspaceId}/workflows")
public class WorkflowController {
    private final WorkflowService workflowService;
    private final LaunchService launchService;

    @PostMapping("/{workflowId}/launch")
    public ResponseEntity<Void> launchWorkflow(@RequestBody @Validated(ValidationSequence.class) LaunchRequest launchRequest, @PathVariable Long workspaceId, @PathVariable Long workflowId){
        launchService.launchWorkflow(workflowId, launchRequest, workspaceId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
    public ResponseEntity<WorkflowResponse> getWorkflow(@PathVariable Long workflowId) {
        var response = workflowService.getWorkflow(workflowId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{workflowId}/sequences")
    public ResponseEntity<List<SequenceResponse>> getSequences(@PathVariable Long workflowId) {
        var response = workflowService.getSequences(workflowId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{workflowId}")
    public ResponseEntity<Void> updateWorkflow(@RequestBody @Validated(ValidationSequence.class) WorkflowRequest request,
                                               @PathVariable Long workflowId,
                                               Authentication authentication) {
        workflowService.updateWorkflow(workflowId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{workflowId}")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable Long workflowId,
                                               Authentication authentication) {
        workflowService.deleteWorkflow(workflowId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
