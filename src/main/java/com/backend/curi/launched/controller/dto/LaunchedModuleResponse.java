package com.backend.curi.launched.controller.dto;

import com.backend.curi.launched.repository.entity.LaunchedModule;
import com.backend.curi.launched.repository.entity.LaunchedStatus;
import com.backend.curi.workflow.repository.entity.ModuleType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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

    private ModuleType type;

    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId contentId;

    private Integer order;

    public static LaunchedModuleResponse of (LaunchedModule launchedModule){
        return new LaunchedModuleResponse(
                launchedModule.getId(),
                launchedModule.getName(),
                launchedModule.getStatus(),
                launchedModule.getType(),
                launchedModule.getContentId(),
                launchedModule.getOrderInSequence()
        );
    }
}
