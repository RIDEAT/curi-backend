package com.backend.curi.launched.controller.dto;

import com.backend.curi.launched.repository.entity.LaunchedStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class LaunchedSequenceUpdateRequest {
    @Pattern(regexp = "^MARKED_AS_COMPLETED$", message = "status must be MARKED_AS_COMPLETED")
    private LaunchedStatus status;

    @Pattern(regexp = "^(\\d{4})-(0[1-9]|1[0-2])-(0[1-9]|[1-2]\\d|3[0-1])$", message = "applyDate must be in the format YYYY-MM-DD and represent a valid date.")
    private LocalDate applyDate;

    private Boolean checkSatisfaction;
}
