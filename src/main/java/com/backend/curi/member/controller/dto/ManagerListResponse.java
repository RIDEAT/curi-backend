package com.backend.curi.member.controller.dto;

import com.backend.curi.member.repository.entity.Employee;
import com.backend.curi.member.repository.entity.Manager;
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
public class ManagerListResponse {
    private String status;
    private List<ManagerResponse> managerList;

    public static ManagerListResponse ofSuccess(List<Manager> managerList) {
        var responseList = managerList.stream()
                .map(ManagerResponse::ofSuccess)
                .collect(Collectors.toList());
        return new ManagerListResponse("success", responseList);
    }
}
