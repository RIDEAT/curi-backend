package com.backend.curi.launched.launchedmodule.controller.dto;

import com.backend.curi.launched.launchedsequence.repository.entity.LaunchedSequence;
import com.backend.curi.launched.launchedmodule.repository.entity.LaunchedModule;
import com.backend.curi.launched.launchedworkflow.repository.entity.LaunchedStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LaunchedModuleResponse {
    private Long id;

    private String name;

    private LaunchedStatus status;

    private LaunchedSequence launchedSequence;

    private ObjectId contentId;

    private Long order;

    public static LaunchedModuleResponse of (LaunchedModule launchedModule){
        return new LaunchedModuleResponse(
                launchedModule.getId(),
                launchedModule.getName(),
                launchedModule.getStatus(),
                launchedModule.getLaunchedSequence(),
                launchedModule.getContentId(),
                launchedModule.getOrderInSequence()
        );
    }
}
