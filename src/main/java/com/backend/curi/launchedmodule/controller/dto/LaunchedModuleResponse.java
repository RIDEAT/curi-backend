package com.backend.curi.launchedmodule.controller.dto;

import com.backend.curi.launchedmodule.repository.entity.LaunchedModule;
import com.backend.curi.launchedsequence.repository.entity.LaunchedSequence;
import com.backend.curi.launchedworkflow.repository.entity.LaunchedStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LaunchedModuleResponse {
    private Long id;

    private String name;

    private LaunchedStatus status;

    private LaunchedSequence launchedSequence;

    private Long mongoId;

    private Long order;

    public static LaunchedModuleResponse of (LaunchedModule launchedModule){
        return new LaunchedModuleResponse(
                launchedModule.getId(),
                launchedModule.getName(),
                launchedModule.getStatus(),
                launchedModule.getLaunchedSequence(),
                launchedModule.getMongoId(),
                launchedModule.getOrderInSequence()
        );
    }
}
