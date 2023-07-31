package com.backend.curi.workspace.controller.dto;

import com.backend.curi.workspace.repository.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoleListResponse {
    private String status;
    List<RoleResponse> roleList;

    public static RoleListResponse of(List<Role> roleList){
        var responseList = roleList.stream()
                .map(RoleResponse::of)
                .collect(Collectors.toList());
        return new RoleListResponse("success", responseList);
    }
}