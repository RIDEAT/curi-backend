package com.backend.curi.member.controller.dto;


import com.backend.curi.launched.repository.entity.LaunchedWorkflowManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeManagerDetail {
    private Long workflowManagerId;
    private String name;
    private String roleName;
    private Long id;
    private Long roleId;

    public static EmployeeManagerDetail of(LaunchedWorkflowManager manager) {
        return new EmployeeManagerDetail(
                manager.getId(),
                manager.getMember().getName(),
                manager.getRole().getName(),
                manager.getMember().getId(),
                manager.getRole().getId());
    }
}
