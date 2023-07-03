package com.backend.curi.workSpace.controller.dto;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
public class WorkSpaceForm {

    @NotNull
    @Size(max = 64)
    private String name;


}
