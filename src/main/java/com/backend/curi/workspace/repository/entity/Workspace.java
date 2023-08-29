package com.backend.curi.workspace.repository.entity;

import com.backend.curi.member.repository.entity.Member;
import com.backend.curi.userworkspace.repository.entity.Userworkspace;
import com.backend.curi.workflow.repository.entity.Workflow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor

public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    @Builder.Default
    private String logoUrl = "default/logo/example_logo.jpeg";

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> members;

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Workflow> workflows = new ArrayList<>();

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL)
    private List<Userworkspace> userworkspaces;

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Role> roles = new ArrayList<>();

    public Workspace(){}
    @Builder
    public Workspace(Long id, String name, String email){
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Workspace update (Workspace workspace){
        this.id = workspace.getId();
        this.name = workspace.getName();
        this.email= workspace.getEmail();
        return this;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Workspace workspace = (Workspace) o;
//        return Objects.equals(id, workspace.id);
//    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
