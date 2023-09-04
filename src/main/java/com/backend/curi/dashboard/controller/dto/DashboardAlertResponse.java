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
    List<MemberAlertResponse> employeeAlerts;
    List<MemberAlertResponse> managerAlerts;

    public static DashboardAlertResponse of( List<MemberAlertResponse> employeeAlertList, List<MemberAlertResponse> managerAlertList){
        return new DashboardAlertResponse(Long.valueOf(employeeAlertList.size()), Long.valueOf(managerAlertList.size()), employeeAlertList, managerAlertList);
    }


}
