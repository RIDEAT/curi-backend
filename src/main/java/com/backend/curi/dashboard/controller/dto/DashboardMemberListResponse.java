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
public class DashboardMemberListResponse {

    private String name;
    private List<DashboardMemberResponse> dashboardMembers;

    public static DashboardMemberListResponse of (String name, List<DashboardMemberResponse> dashboardMemberResponseList){
        return new DashboardMemberListResponse(name,dashboardMemberResponseList);
    }
}
