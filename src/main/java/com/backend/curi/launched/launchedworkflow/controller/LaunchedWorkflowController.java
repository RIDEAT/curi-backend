package com.backend.curi.launched.launchedworkflow.controller;

import com.backend.curi.launched.launchedworkflow.controller.dto.LaunchedWorkflowRequest;
import com.backend.curi.launched.launchedworkflow.controller.dto.LaunchedWorkflowResponse;
import com.backend.curi.launched.launchedworkflow.service.LaunchedWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workspaces/{workspaceId}/launchedworkflows")
// 앞 단에서 공통적으로 workspace에 접근 권한을 확인
@RequiredArgsConstructor
public class LaunchedWorkflowController {

    private final LaunchedWorkflowService launchedWorkflowService;
    @GetMapping("/{launchedworkflowId}")
    public ResponseEntity<LaunchedWorkflowResponse> getLaunchedWorkflow(@PathVariable Long workspaceId, @PathVariable Long launchedworkflowId) {
        // Here, you can implement the logic to retrieve a specific launched workflow by its ID within the specified workspaceId.
        System.out.println("Launch Controller");
        LaunchedWorkflowResponse launchedWorkflow = launchedWorkflowService.getLaunchedWorkflow(workspaceId, launchedworkflowId);
        return ResponseEntity.ok(launchedWorkflow);
    }


    @PostMapping
    public ResponseEntity<LaunchedWorkflowResponse> createLaunchedWorkflow(@PathVariable Long workspaceId, @RequestBody LaunchedWorkflowRequest launchedWorkflowRequest) {
        LaunchedWorkflowResponse createdLaunchedWorkflow = launchedWorkflowService.createLaunchedWorkflow(workspaceId, launchedWorkflowRequest);
        return ResponseEntity.ok(createdLaunchedWorkflow);
    }

    /*
    @PutMapping("/{launchedworkflowId}")
    public ResponseEntity<LaunchedWorkflowResponse> updateLaunchedWorkflow(@PathVariable String workspaceId, @PathVariable Long launchedworkflowId, @RequestBody LaunchedWorkflow updatedLaunchedWorkflow) {
        // Here, you can implement the logic to update a specific launched workflow by its ID within the specified workspaceId.
        LaunchedWorkflowResponse updatedLaunchedWorkflow = launchedWorkflowService.updateLaunchedWorkflow(workspaceId, launchedworkflowId, updatedLaunchedWorkflow);
        return ResponseEntity.ok(updatedLaunchedWorkflow);

    }

    @DeleteMapping("/{launchedworkflowId}")
    public ResponseEntity<Void> deleteLaunchedWorkflow(@PathVariable String workspaceId, @PathVariable Long launchedworkflowId) {
        // Here, you can implement the logic to delete a specific launched workflow by its ID within the specified workspaceId.
        boolean deleted = launchedWorkflowService.deleteLaunchedWorkflow(workspaceId, launchedworkflowId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }*/
}
