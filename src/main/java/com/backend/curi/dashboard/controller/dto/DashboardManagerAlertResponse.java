package com.backend.curi.dashboard.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardManagerAlertResponse {
    private String name;
    private Long overdue;

    private String sequence;
    private String workflow;

}
