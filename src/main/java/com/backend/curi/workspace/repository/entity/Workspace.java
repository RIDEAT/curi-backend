package com.backend.curi.workspace.repository.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.Objects;

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


    public Workspace(){}
    @Builder
    public Workspace(int workspaceId, String name, String email){
        this.workspaceId = workspaceId;
        this.name = name;
        this.email = email;
    }

    public Workspace update (Workspace workspace){
        this.workspaceId = workspace.getWorkspaceId();
        this.name = workspace.getName();
        this.email= workspace.getEmail();
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workspace workspace = (Workspace) o;
        return Objects.equals(workspaceId, workspace.workspaceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workspaceId);
    }
}
