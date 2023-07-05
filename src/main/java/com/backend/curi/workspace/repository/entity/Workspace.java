package com.backend.curi.workspace.repository.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
@Setter
@Getter
@Entity
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int workspaceId;

    @Column
    private String name;

    public Workspace(){}
    @Builder
    public Workspace(int workspaceId, String name){
        this.workspaceId = workspaceId;
        this.name = name;
    }



}
