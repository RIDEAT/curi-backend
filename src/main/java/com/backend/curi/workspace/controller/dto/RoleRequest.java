package com.backend.curi.workspace.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
public class RoleRequest {
    @NotNull
    @Size(min = 1, max = 20)
    @Schema(description = "name")
    private String name;

}