package com.backend.curi.workflow.repository.entity;

import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@NoArgsConstructor
@Getter
@Entity
public class Workflow extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @Builder
    public Workflow(Long workflowId, String name, Workspace workspace){
        this.id = workflowId;
        this.name = name;
        this.workspace = workspace;
    }

}
