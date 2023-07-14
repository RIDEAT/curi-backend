package com.backend.curi.member.controller.dto;

import com.backend.curi.member.repository.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployeeListResponse {

    private String status;
    private List<EmployeeResponse> employeeList;

    public static EmployeeListResponse ofSuccess(List<Employee> employeeList) {
        var responseList = employeeList.stream()
                .map(EmployeeResponse::ofSuccess)
                .collect(Collectors.toList());
        return new EmployeeListResponse("success", responseList);
    }
}
