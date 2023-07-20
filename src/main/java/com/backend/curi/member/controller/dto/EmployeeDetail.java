package com.backend.curi.member.controller.dto;

import com.backend.curi.member.repository.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDetail {
    private String startDate;
    List<EmployeeManagerDetail> managers;

    public static EmployeeDetail of(Employee employee) {
        var managerList = employee.getEmployeeManagers().stream()
                .map(EmployeeManagerDetail::of)
                .collect(Collectors.toList());
        return new EmployeeDetail(employee.getStartDate().toString(), managerList);
    }
}
