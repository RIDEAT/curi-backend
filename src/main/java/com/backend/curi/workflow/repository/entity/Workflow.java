package com.backend.curi.workflow.repository.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Workflow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int workflowId;

    @Column
    private String name;

    @Column
    private Long workspaceId;

    public Workflow(){}

    @Builder
    public Workflow(int workflowId, String name, Long workspaceId){
        this.workflowId = workflowId;
        this.name = name;
        this.workspaceId = workspaceId;
    }

}
