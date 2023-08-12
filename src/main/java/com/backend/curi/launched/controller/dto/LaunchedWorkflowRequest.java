package com.backend.curi.launched.controller.dto;

import com.backend.curi.launched.repository.entity.LaunchedStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LaunchedWorkflowRequest
{
    @NotNull
    @Size(max = 50, min = 2)
    private String name;

    @NotNull
    private LaunchedStatus status;

    @NotNull
    @Pattern(regexp = "^(\\d{4})-(0[1-9]|1[0-2])-(0[1-9]|[1-2]\\d|3[0-1])$", message = "keyDate must be in the format YYYY-MM-DD and represent a valid date.")
    private LocalDate keyDate;

    @NotNull
    @Min(value = 1, message = "employeeId must be greater than or equal to 1")
    private Long employeeId;

    @NotNull
    @Min(value = 1, message = "workflowId must be greater than or equal to 1")
    private Long workflowId;

}
