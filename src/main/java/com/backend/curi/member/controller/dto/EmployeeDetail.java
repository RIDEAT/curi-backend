package com.backend.curi.member.controller.dto;

import com.backend.curi.member.repository.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDetail {
    private String startDate;

    public static EmployeeDetail of(Employee employee) {
        return new EmployeeDetail(employee.getStartDate().toString());
    }
}
