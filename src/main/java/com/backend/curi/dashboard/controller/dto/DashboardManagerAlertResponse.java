package com.backend.curi.dashboard.controller.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardManagerAlertResponse {
    private String name;
    private Long overdue;

    private String sequence;
    private String workflow;

}
