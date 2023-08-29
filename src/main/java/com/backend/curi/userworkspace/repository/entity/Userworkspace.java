package com.backend.curi.userworkspace.repository.entity;

import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.user.repository.entity.User_;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Userworkspace extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User_ user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspaceId",nullable = false, updatable = false)
    private Workspace workspace;

    @Builder
    public Userworkspace(Long id, User_ user, Workspace workspace){
        this.id = id;
        this.user = user;
        this.workspace = workspace;
    }

    public void setUser(User_ user){
        this.user = user;
    }
}
