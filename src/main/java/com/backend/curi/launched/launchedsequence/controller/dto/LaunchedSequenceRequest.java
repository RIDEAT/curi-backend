package com.backend.curi.launched.launchedsequence.controller.dto;

import com.backend.curi.launched.launchedworkflow.repository.entity.LaunchedStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.*;

@AllArgsConstructor
@Getter
public class LaunchedSequenceRequest {
    @NotNull
    @Size(max = 50, min = 2)
    private String name;

    @NotNull
    @Pattern(regexp = "^(ACTIVE|PENDING|COMPLETED)$")
    private LaunchedStatus status;

    @NotNull
    @Pattern(regexp = "^(\\d{4})-(0[1-9]|1[0-2])-(0[1-9]|[1-2]\\d|3[0-1])$", message = "applyDate must be in the format YYYY-MM-DD and represent a valid date.")
    private String applyDate;

    @NotNull
    @Min(value = 1, message = "employeeId must be greater than or equal to 1")
    private Long employeeId;

    @NotNull
    @Min(value = 1, message = "workflowId must be greater than or equal to 1")
    private Long workflowId;

    @NotNull
    @Min(value = 1, message = "order must be greater than or equal to 1")
    @Max(value = 100, message = "order must be less than or equal to 100")
    private Long order;

}
