package com.backend.curi.user.repository.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
public class User_ {
    @Id
    private String userId;

    @Column
    private String email;

    @Column
    private int workSpaceId;


    // workspaceId 는 자연수이다.
    // workspaceId 가 0 인 경우, workspaceId를 아직 할당하지 못한 경우이다.

    public User_(){}

    @Builder
    public User_(String userId, String email, int workSpaceId){
        this.userId = userId;
        this.email = email;
        this.workSpaceId = workSpaceId;
    }
}
