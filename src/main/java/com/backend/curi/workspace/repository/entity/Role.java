package com.backend.curi.workspace.repository.entity;


import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.workspace.controller.dto.RoleRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    private String name;

    public Role modify(RoleRequest roleRequest){
        this.name = roleRequest.getName();
        return this;
    }
}
