package com.backend.curi.member.controller.dto;


import com.backend.curi.member.repository.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployeeResponse {
    private String status;

    private Long id;
    private String name;
    private String email;
    private String phoneNum;
    private String startDate;

    public static EmployeeResponse ofSuccess(Employee employee) {
        return new EmployeeResponse("success",
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getPhoneNum(),
                employee.getStartDate().toString());
    }
}
