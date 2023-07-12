package com.backend.curi.userworkspace.repository.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
public class Userworkspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userworkspaceId;

    @Column
    private String userId;

    @Column String userEmail;

    @Column
    private int workspaceId;

    public Userworkspace() {}

    @Builder
    public Userworkspace(int userworkspaceId, String userId, String userEmail, int workspaceId){
        this.userworkspaceId = userworkspaceId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.workspaceId = workspaceId;
    }

}
