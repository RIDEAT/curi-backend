package com.backend.curi.workflow.repository.entity;


import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.workflow.controller.dto.SequenceRequest;
import com.backend.curi.workspace.repository.entity.Role;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sequence extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Setter
    private String name;

    @Setter
    private Integer dayOffset;

    @Setter
    @ColumnDefault("true")
    private Boolean checkSatisfaction;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;

    @OneToMany(mappedBy = "sequence", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Module> modules = new ArrayList<>();

    public static Sequence of(SequenceRequest request, Role role, Workspace workspace, Workflow workflow) {
        workflow.updateUpdatedDate();
        return Sequence.builder().
                name(request.getName()).
                role(role).
                dayOffset(request.getDayOffset()).
                checkSatisfaction(request.getCheckSatisfaction()).
                workspace(workspace).
                workflow(workflow).
                build();
    }

    public static Sequence of(Sequence origin, Role role, Workspace workspace, Workflow workflow) {
        workflow.updateUpdatedDate();
        return Sequence.builder().
                name(origin.getName()).
                role(role).
                dayOffset(origin.getDayOffset()).
                checkSatisfaction(origin.getCheckSatisfaction()).
                workspace(workspace).
                workflow(workflow).
                build();
    }


    public void updateUpdatedDate() {
        this.updatedDate = LocalDateTime.now();
        this.workflow.updateUpdatedDate();
    }

    public void modify(SequenceRequest request, Role role){
        this.name = request.getName();
        this.role = role;
        this.dayOffset = request.getDayOffset();
        updateUpdatedDate();
    }
}
