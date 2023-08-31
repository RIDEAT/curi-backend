package com.backend.curi.frontoffice.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class SequenceSatisfactionRequest {
    @NotNull
    @Min(1)
    @Max(10)
    private Long score;

    @NotNull
    @Size(min = 0, max = 512, message = "평가는 256자 이내여야 합니다!")
    private String comment;
}
