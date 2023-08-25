package com.backend.curi.workflow.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRoleRequest {
    @NotNull
    Long memberId;
    @NotNull
    Long RoleId;

}
