package com.backend.curi.dashboard.controller;

import com.backend.curi.dashboard.controller.dto.*;
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
        DashboardAlertResponse mockResponse = new DashboardAlertResponse();
        mockResponse.setEmployeeAlertCnt(2L);
        mockResponse.setManagerAlertCnt(2L);
        mockResponse.setEmployeeAlerts(List.of(
                DashboardEmployeeAlertResponse.builder()
                        .name("김철수")
                        .overdue(3L)
                        .sequence("코드 분석")
                        .workflow("IT 워크플로우")
                        .build(),
                DashboardEmployeeAlertResponse.builder()
                        .name("박희수")
                        .overdue(5L)
                        .sequence("사내 성희롱 예방 교육")
                        .workflow("공통 워크플로우")
                        .build()));

        mockResponse.setManagerAlerts(List.of(
                DashboardManagerAlertResponse.builder()
                        .name("김영희")
                        .overdue(3L)
                        .sequence("코드 리뷰")
                        .workflow("IT 워크플로우")
                        .build(),
                DashboardManagerAlertResponse.builder()
                        .name("박영수")
                        .overdue(5L)
                        .sequence("신입사원과 식사시간")
                        .workflow("공통 워크플로우")
                        .build())
        );


        return ResponseEntity.ok(mockResponse);
      //  return ResponseEntity.ok(dashboardService.getDashboardAlertResponse(workspaceId));
    }
}
