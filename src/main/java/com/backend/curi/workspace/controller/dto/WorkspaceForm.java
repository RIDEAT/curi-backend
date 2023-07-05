package com.backend.curi.workspace.controller.dto;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
public class WorkspaceForm {

    @NotNull
    @Size(max = 64)
    private String name;


}
