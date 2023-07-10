package com.backend.curi.workspace.controller.dto;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
public class WorkspaceForm {

    @NotNull
    @Size(min = 2, max = 20)
    private String name;

    @Email
    private String email;


}
