package com.backend.curi.workspace.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceRequest {
    @NotNull
    @Size(min = 2, max = 20)
    @Schema(description = "name")
    private String name;


    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Schema(description = "email")
    private String email;
}
