package com.backend.curi.common.feign.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SequenceMessageRequest {
    @NotNull
    private Long id;

    @NotNull
    private LocalDateTime applyDate;
}
