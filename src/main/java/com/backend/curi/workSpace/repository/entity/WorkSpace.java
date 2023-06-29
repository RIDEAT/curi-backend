package com.backend.curi.workSpace.repository.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
@Setter
@Getter
@Entity
public class WorkSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String workSpaceId;

    @Column
    private String name;

    @Builder
    public WorkSpace(String workSpaceId, String name){
        this.workSpaceId = workSpaceId;
        this.name = name;
    }



}
