package com.backend.curi.member.controller.dto;

import com.backend.curi.member.repository.entity.Employee;
import com.backend.curi.member.repository.entity.Manager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManagerResponse {
    private Long id;
    private String name;
    private String email;
    private String phoneNum;
    private String startDate;

    public static ManagerResponse of(Manager manager) {
        return new ManagerResponse(
                manager.getId(),
                manager.getName(),
                manager.getEmail(),
                manager.getPhoneNum(),
                manager.getStartDate().toString());
    }
}