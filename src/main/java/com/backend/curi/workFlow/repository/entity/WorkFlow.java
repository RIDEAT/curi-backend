package com.backend.curi.workFlow.repository.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class WorkFlow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int workFlowId;

    @Column
    private String name;

    @Column
    private int workSpaceId;

    public WorkFlow(){}

    @Builder
    public WorkFlow(int workFlowId, String name, int workSpaceId){
        this.workFlowId = workFlowId;
        this.name = name;
        this.workSpaceId = workSpaceId;
    }

}
