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
    private Long id;
    private String name;
    private String email;
    private String phoneNum;
    private String startDate;

    public static EmployeeResponse of(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getPhoneNum(),
                employee.getStartDate().toString());
    }
}
