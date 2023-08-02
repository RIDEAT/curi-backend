package com.backend.curi.workflow.controller.dto;

import com.backend.curi.member.repository.entity.EmployeeManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LaunchRequest {
    @NotNull(message = "해당 워크플로우를 실행할 사람의 member id를 작성해주세요.")
    Long memberId;

    @NotNull(message = "key date를 작성해주세요.")
    LocalDate keyDate;
}
