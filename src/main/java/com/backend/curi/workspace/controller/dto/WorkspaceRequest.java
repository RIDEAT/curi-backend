package com.backend.curi.workspace.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
public class WorkspaceRequest {
    private Long id;
    @NotNull
    @Size(min = 2, max = 20)
    @Schema(description = "name")
    private String name;

    @Email
    @Schema(description = "email")
    private String email;
}
