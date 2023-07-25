package com.backend.curi.launchedworkflow.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
public class LaunchedWorkflowRequest
{
    @NotNull
    @Size(max = 50, min = 2)
    private String name;

    @NotNull
    @Pattern(regexp = "^(ACTIVE|PENDING|COMPLETED)$")
    private String status;

    @NotNull
    @Pattern(regexp = "^(\\d{4})-(0[1-9]|1[0-2])-(0[1-9]|[1-2]\\d|3[0-1])$", message = "keyDate must be in the format YYYY-MM-DD and represent a valid date.")
    private String keyDate;

    @NotNull
    @Min(value = 1, message = "employeeId must be greater than or equal to 1")
    private Long employeeId;

    @NotNull
    @Min(value = 1, message = "workflowId must be greater than or equal to 1")
    private Long workflowId;

}
