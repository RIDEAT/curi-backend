package com.backend.curi.workflow.controller.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class WorkflowRequest {
    @NotBlank(message = "워크플로우의 이름을 작성해주세요.")
    @Size(min = 1, max = 30, message = "이름은 1 ~ 30자 이내여야 합니다!")
    private String name;
}
