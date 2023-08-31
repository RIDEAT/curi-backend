package com.backend.curi.launched.controller.dto;

import com.backend.curi.member.controller.dto.EmployeeManagerDetail;
import com.backend.curi.member.controller.dto.MemberResponse;
import com.backend.curi.member.repository.entity.MemberType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LaunchedEmployeeResponse {
    private Long id;
    private Long launchedWorkflowId;
    private String name;
    private String email;
    private String department;
    private String phoneNum;
    private MemberType type;
    private LocalDate keyDate;
    private List<EmployeeManagerDetail> managers;

    public static LaunchedEmployeeResponse of(LaunchedWorkflowResponse response){

        return new LaunchedEmployeeResponse(
                response.getEmployee().getId(),
                response.getId(),
                response.getEmployee().getName(),
                response.getEmployee().getEmail(),
                response.getEmployee().getDepartment(),
                response.getEmployee().getPhoneNum(),
                response.getEmployee().getType(),
                response.getKeyDate(),
                response.getRoleDetails()
        );
    }
}
