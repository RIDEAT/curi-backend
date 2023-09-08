package com.backend.curi.workflow.controller.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ModuleUpdateRequest {
    @Size(min = 1, max = 40, message = "이름은 1 ~ 20자 이내여야 합니다!")
    private String name;

    private Integer order;
}
