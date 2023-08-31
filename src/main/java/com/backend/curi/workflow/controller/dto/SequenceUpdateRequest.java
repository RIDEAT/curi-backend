package com.backend.curi.workflow.controller.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class SequenceUpdateRequest {
    @Size(min = 1, max = 20, message = "이름은 1 ~ 20자 이내여야 합니다!")
    private String name;
    private Long roleId;
    private Integer dayOffset;
    private Boolean checkSatisfaction;
}
