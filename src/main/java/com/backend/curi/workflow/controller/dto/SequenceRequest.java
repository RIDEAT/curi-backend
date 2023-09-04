package com.backend.curi.workflow.controller.dto;

import com.backend.curi.workspace.repository.entity.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class SequenceRequest {
    @NotBlank(message = "시퀀스의 이름을 작성해주세요.")
    @Size(min = 1, max = 20, message = "이름은 1 ~ 20자 이내여야 합니다!")
    private String name;

    @NotNull(message = "시퀀스를 수행할 대상을 지정 해주세요.")
    private Long roleId;

    private Integer dayOffset;

    private Boolean checkSatisfaction = true;

    private Long prevSequenceId = 0L;
}
