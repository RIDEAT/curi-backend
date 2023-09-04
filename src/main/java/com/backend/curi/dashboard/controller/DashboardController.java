package com.backend.curi.dashboard.controller;

import com.backend.curi.dashboard.controller.dto.*;
import com.backend.curi.dashboard.service.DashboardService;
import com.backend.curi.member.repository.entity.MemberType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("/workspaces/{workspaceId}/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/workflows")
    public ResponseEntity<List<DashboardWorkflowResponse>> getDashboardWorkflowList(@PathVariable Long workspaceId) {
        return ResponseEntity.ok(dashboardService.getDashboardWorkflowResponseList(workspaceId));
    }

    @GetMapping("/workflows/{workflowId}/members")
    public ResponseEntity<DashboardMemberListResponse> getDashboardMemberList(@PathVariable Long workspaceId, @PathVariable Long workflowId) {
        return ResponseEntity.ok(dashboardService.getDashboardMemberListResponse(workflowId));
    }

    @GetMapping("/alerts")
    public ResponseEntity<DashboardAlertResponse> getAlertList(@PathVariable Long workspaceId) {
        DashboardAlertResponse mockResponse = new DashboardAlertResponse();
        mockResponse.setEmployeeAlertCnt(2L);
        mockResponse.setManagerAlertCnt(2L);
        mockResponse.setEmployeeAlerts(List.of(
                MemberAlertResponse.mock(
                        "김철수",
                        "코드 분석",
                        "IT 워크플로우",
                        "신입사원",
                        3L),
                MemberAlertResponse.mock(
                        "박희수",
                        "사내 성희롱 예방 교육",
                        "공통 워크플로우",
                        "신입사원",
                        5L)));

        mockResponse.setManagerAlerts(List.of(
                MemberAlertResponse.mock(
                        "김영희",
                        "코드 분석",
                        "IT 워크플로우",
                        "담당사수",
                        3L),
                MemberAlertResponse.mock(
                        "박영수",
                        "신입사원과 식사 시간",
                        "공통 워크플로우",
                        "매니저",
                        5L))
        );


        return ResponseEntity.ok(mockResponse);
        //  return ResponseEntity.ok(dashboardService.getDashboardAlertResponse(workspaceId));
    }

    @GetMapping("/alerts/overdue")
    public ResponseEntity<List<MemberAlertResponse>> getAlert(@PathVariable Long workspaceId, @RequestParam("type") MemberType type) {
        return ResponseEntity.ok(dashboardService.getMemberAlerts(type, workspaceId));
    }
}
