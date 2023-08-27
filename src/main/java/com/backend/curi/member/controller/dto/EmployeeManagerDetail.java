package com.backend.curi.member.controller.dto;


import com.backend.curi.launched.repository.entity.LaunchedWorkflowManager;
import com.backend.curi.member.repository.entity.Employee;
import com.backend.curi.member.repository.entity.EmployeeManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeManagerDetail {
    private String name;
    private String roleName;
    private Long id;
    private Long roleId;

    public static EmployeeManagerDetail of(EmployeeManager manager) {
        return new EmployeeManagerDetail(
                manager.getManager().getMember().getName(),
                manager.getRole().getName(),
                manager.getManager().getMember().getId(),
                manager.getRole().getId());
    }

    public static EmployeeManagerDetail of(LaunchedWorkflowManager manager) {
        return new EmployeeManagerDetail(
                manager.getMember().getName(),
                manager.getRole().getName(),
                manager.getMember().getId(),
                manager.getRole().getId());
    }
}
