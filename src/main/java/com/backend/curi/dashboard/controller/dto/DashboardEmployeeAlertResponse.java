package com.backend.curi.dashboard.controller.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardEmployeeAlertResponse {
    private String name;
    private Long overdue;

    private String sequence;
    private String workflow;

}
