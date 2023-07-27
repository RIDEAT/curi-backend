package com.backend.curi.workflow.controller.dto;
import com.backend.curi.workflow.repository.entity.Module;
import com.backend.curi.workflow.repository.entity.ModuleType;
import com.backend.curi.workflow.repository.entity.SequenceModule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModuleResponse {
    private Long id;
    private String name;
    private ModuleType type;
    private ObjectId contentId;

    public static ModuleResponse of(Module module) {
        return new ModuleResponse(
                module.getId(),
                module.getName(),
                module.getType(),
                module.getContentId());
    }
}
