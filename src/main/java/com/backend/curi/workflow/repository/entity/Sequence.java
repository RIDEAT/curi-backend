package com.backend.curi.workflow.repository.entity;


import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.workflow.controller.dto.SequenceRequest;
import com.backend.curi.workspace.repository.entity.Role;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sequence extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @OneToMany(mappedBy = "sequence", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WorkflowSequence> workflowSequences = new ArrayList<>();

    @OneToMany(mappedBy = "sequence", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SequenceModule> sequenceModules = new ArrayList<>();

    public static Sequence of(SequenceRequest request, Role role, Workspace workspace) {
        return Sequence.builder().
                name(request.getName()).
                role(role).
                workspace(workspace).
                build();
    }

    public void modify(SequenceRequest request, Role role) {
        this.name = request.getName();
        this.role = role;
    }
}
