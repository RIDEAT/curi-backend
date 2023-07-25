package com.backend.curi.launchedmodule.controller.dto;

import com.backend.curi.launchedsequence.repository.entity.LaunchedSequence;
import com.backend.curi.launchedworkflow.repository.entity.LaunchedStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.*;

@AllArgsConstructor
@Getter
public class LaunchedModuleRequest {
    @NotNull
    @Size(max = 50, min = 2)
    private String name;

    @NotNull
    @Pattern(regexp = "^(ACTIVE|PENDING|COMPLETED)$")
    private LaunchedStatus status;

    @NotNull
    @Min(value = 1, message = "launchedSequence Id must be greater than or equal to 1")
    private Long launchedSequenceId;

    @NotNull
    @Min(value = 1, message = "mongo Id must be greater than or equal to 1")
    private Long mongoId;

    @NotNull
    @Min(value = 1, message = "order must be greater than or equal to 1")
    @Max(value = 100, message = "order must be less than or equal to 100")
    private Long order;
}
