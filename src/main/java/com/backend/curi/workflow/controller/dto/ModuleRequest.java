package com.backend.curi.workflow.controller.dto;

import com.backend.curi.workflow.repository.entity.ModuleType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ModuleRequest {
    @NotBlank(message = "모듈의 이름을 작성해주세요.")
    @Size(min = 1, max = 40, message = "이름은 1 ~ 20자 이내여야 합니다!")
    private String name;

    @NotNull
    private ModuleType type;

    @NotNull
    private Integer order;
}
