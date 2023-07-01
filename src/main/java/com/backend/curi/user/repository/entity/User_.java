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

    @Builder
    public User_(String userId, String email, int workSpaceId){
        this.userId = userId;
        this.email = email;
        this.workSpaceId = workSpaceId;
    }
}
