package com.backend.curi.dashboard.controller;

import com.backend.curi.dashboard.controller.dto.DashboardEmployeeAlertResponse;
import com.backend.curi.dashboard.controller.dto.DashboardMemberListResponse;
import com.backend.curi.dashboard.controller.dto.DashboardAlertResponse;
import com.backend.curi.dashboard.controller.dto.DashboardWorkflowResponse;
import com.backend.curi.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/workspaces/{workspaceId}/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/workflows")
    public ResponseEntity<List<DashboardWorkflowResponse>> getDashboardWorkflowList(@PathVariable Long workspaceId){
        return ResponseEntity.ok(dashboardService.getDashboardWorkflowResponseList(workspaceId));
    }

    @GetMapping("/workflows/{workflowId}/members")
    public ResponseEntity<DashboardMemberListResponse> getDashboardMemberList(@PathVariable Long workspaceId, @PathVariable Long workflowId){
        return ResponseEntity.ok(dashboardService.getDashboardMemberListResponse(workflowId));
    }

    @GetMapping("/alerts")
    public ResponseEntity<DashboardAlertResponse> getAlertList(@PathVariable Long workspaceId){
        return ResponseEntity.ok(dashboardService.getDashboardAlertResponse(workspaceId));
    }
}
