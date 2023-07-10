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

    @Column
    private String email;

    @Column String userId;

    public Workspace(){}
    @Builder
    public Workspace(int workspaceId, String name, String userId, String email){
        this.workspaceId = workspaceId;
        this.name = name;
        this.userId = userId;
        this.email = email;
    }

    public Workspace update (Workspace workspace){
        this.workspaceId = workspace.getWorkspaceId();
        this.name = workspace.getName();
        this.email= workspace.getEmail();
        this.userId = workspace.getUserId();
        return this;
    }



}
