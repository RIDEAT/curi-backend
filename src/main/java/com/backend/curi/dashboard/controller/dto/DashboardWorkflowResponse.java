package com.backend.curi.dashboard.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardWorkflowResponse {
    private String name;

    private Long id;
    private Long pendingCnt;
    private Long inProgressCnt;
    private Long completedCnt;
    private Long progress;
}
