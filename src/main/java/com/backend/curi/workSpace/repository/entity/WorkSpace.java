package com.backend.curi.workSpace.repository.entity;

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
public class WorkSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int workSpaceId;

    @Column
    private String name;

    public WorkSpace(){}
    @Builder
    public WorkSpace(int workSpaceId, String name){
        this.workSpaceId = workSpaceId;
        this.name = name;
    }



}
