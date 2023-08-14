package com.backend.curi.dashboard.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAlertResponse {

    Long employeeAlertCnt;
    Long managerAlertCnt;
    List<DashboardEmployeeAlertResponse> employeeAlerts;
    List<DashboardManagerAlertResponse> managerAlerts;

    public static DashboardAlertResponse of( List<DashboardEmployeeAlertResponse> employeeAlertList, List<DashboardManagerAlertResponse> managerAlertList){
        return new DashboardAlertResponse(Long.valueOf(employeeAlertList.size()), Long.valueOf(managerAlertList.size()), employeeAlertList, managerAlertList);
    }


}
