package com.backend.curi.launched.controller.dto;

import com.backend.curi.launched.repository.entity.LaunchedStatus;
import com.backend.curi.workflow.repository.entity.ModuleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.types.ObjectId;

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
    @Pattern(regexp = "^(notification|contents|survey|finished|slack|google_docs|notion|youtube|google_form|google_drive|web_url)")
    private ModuleType type;

    @NotNull
    @Min(value = 1, message = "launchedSequence Id must be greater than or equal to 1")
    private Long launchedSequenceId;

    @NotNull
    @Min(value = 1, message = "enter content id")
    private ObjectId contentId;

    @NotNull
    @Min(value = 1, message = "order must be greater than or equal to 1")
    @Max(value = 100, message = "order must be less than or equal to 100")
    private Integer order;
}
