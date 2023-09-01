package com.backend.curi.workflow.controller.dto;

import com.backend.curi.workflow.repository.entity.Sequence;
import com.backend.curi.workspace.controller.dto.RoleResponse;
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
    private Integer dayOffset;
    private Boolean checkSatisfaction;
    private List<ModuleResponse> modules;

    public static SequenceResponse of(Sequence sequence) {
        var sequenceModules = sequence.getModules();
            sequenceModules.sort((a, b) -> a.getOrder() - b.getOrder());
        var responseList = sequenceModules.stream()
                .map(ModuleResponse::of)
                .collect(Collectors.toList());
        return new SequenceResponse(sequence.getId(),
                sequence.getName(),
                RoleResponse.of(sequence.getRole()),
                sequence.getDayOffset(),
                sequence.getCheckSatisfaction(),
                responseList);
    }
}
