package com.backend.curi.launched.controller;

import com.backend.curi.launched.controller.dto.LaunchedWorkflowRequest;
import com.backend.curi.launched.controller.dto.LaunchedWorkflowResponse;
import com.backend.curi.launched.controller.dto.LaunchedWorkflowUpdateRequest;
import com.backend.curi.launched.service.LaunchedWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workspaces/{workspaceId}/launchedworkflows")
// 앞 단에서 공통적으로 workspace에 접근 권한을 확인
@RequiredArgsConstructor
public class LaunchedWorkflowController {

    private final LaunchedWorkflowService launchedWorkflowService;

    @GetMapping
    public ResponseEntity<List<LaunchedWorkflowResponse>> getLaunchedWorkflowList(@PathVariable Long workspaceId){
        List<LaunchedWorkflowResponse> launchedWorkflowResponse = launchedWorkflowService.getLaunchedWorkflowList(workspaceId);
        return ResponseEntity.ok(launchedWorkflowResponse);
    }
    @GetMapping("/{launchedworkflowId}")
    public ResponseEntity<LaunchedWorkflowResponse> getLaunchedWorkflow(@PathVariable Long workspaceId, @PathVariable Long launchedworkflowId) {
        // Here, you can implement the logic to retrieve a specific launched workflow by its ID within the specified workspaceId.
        System.out.println("Launch Controller");
        LaunchedWorkflowResponse launchedWorkflow = launchedWorkflowService.getLaunchedWorkflow(launchedworkflowId);
        return ResponseEntity.ok(launchedWorkflow);
    }


    @PostMapping
    public ResponseEntity<LaunchedWorkflowResponse> createLaunchedWorkflow(@PathVariable Long workspaceId, @RequestBody LaunchedWorkflowRequest launchedWorkflowRequest) {
        LaunchedWorkflowResponse createdLaunchedWorkflow = launchedWorkflowService.createLaunchedWorkflow(workspaceId, launchedWorkflowRequest);
        return ResponseEntity.ok(createdLaunchedWorkflow);
    }


    @PutMapping("/{launchedworkflowId}")
    public ResponseEntity<LaunchedWorkflowResponse> modifyLaunchedWorkflow(@PathVariable Long workspaceId, @PathVariable Long launchedworkflowId, @RequestBody LaunchedWorkflowRequest launchedWorkflowRequest) {
        // Here, you can implement the logic to update a specific launched workflow by its ID within the specified workspaceId.
        LaunchedWorkflowResponse updatedLaunchedWorkflow = launchedWorkflowService.modifyLaunchedWorkflow(workspaceId, launchedworkflowId, launchedWorkflowRequest);
        return ResponseEntity.ok(updatedLaunchedWorkflow);
    }

    @PatchMapping("/{launchedworkflowId}")
    public ResponseEntity<LaunchedWorkflowResponse> updateLaunchedWorkflow(@PathVariable Long workspaceId, @PathVariable Long launchedworkflowId, @RequestBody LaunchedWorkflowUpdateRequest request) {
        // Here, you can implement the logic to update a specific launched workflow by its ID within the specified workspaceId.
        LaunchedWorkflowResponse updatedLaunchedWorkflow = launchedWorkflowService.updateLaunchedWorkflow(workspaceId, launchedworkflowId, request);
        return ResponseEntity.ok(updatedLaunchedWorkflow);
    }

    @DeleteMapping("/{launchedworkflowId}")
    public ResponseEntity<Void> deleteLaunchedWorkflow(@PathVariable Long workspaceId, @PathVariable Long launchedworkflowId) {
        launchedWorkflowService.deleteLaunchedWorkflow(launchedworkflowId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
