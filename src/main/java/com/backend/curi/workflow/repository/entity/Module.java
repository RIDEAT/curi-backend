package com.backend.curi.workflow.repository.entity;

import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.workflow.controller.dto.ModuleRequest;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.*;
import org.bson.types.ObjectId;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@Table(name = "module_")
public class Module extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    Integer order;

    @Enumerated(EnumType.STRING)
    private ModuleType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sequence_id")
    private Sequence sequence;

    @Column(nullable = false)
    private ObjectId contentId;

    public static Module of(ModuleRequest request, Workspace workspace,Sequence sequence, ObjectId contentId){
        return Module.builder()
                .name(request.getName())
                .type(request.getType())
                .order(request.getOrder())
                .workspace(workspace)
                .sequence(sequence)
                .contentId(contentId).build();
    }

    public void updateUpdatedDate() {
        this.updatedDate = LocalDateTime.now();
        this.sequence.updateUpdatedDate();
    }

    public void modify(ModuleRequest request){
        this.name = request.getName();
        this.type = request.getType();
        this.order = request.getOrder();
        updateUpdatedDate();
    }
}