package com.backend.curi.workflow.controller.dto;

import com.backend.curi.workflow.repository.entity.Sequence;
import com.backend.curi.workflow.repository.entity.SequenceModule;
import com.backend.curi.workflow.repository.entity.WorkflowSequence;
import com.backend.curi.workspace.controller.dto.RoleResponse;
import com.backend.curi.workspace.repository.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SequenceResponse {
    private Long id;
    private String name;
    private RoleResponse role;
    private List<ModuleResponse> modules;

    public static SequenceResponse of(Sequence sequence) {
        var modules =
                sequence.getSequenceModules();
        modules.sort((a, b) -> a.getOrder() - b.getOrder());
        var responseList = modules.stream()
                .map(SequenceModule::getModule)
                .map(ModuleResponse::of)
                .collect(Collectors.toList());
        return new SequenceResponse(sequence.getId(),
                sequence.getName(),
                RoleResponse.of(sequence.getRole()),
                responseList);
    }
}
