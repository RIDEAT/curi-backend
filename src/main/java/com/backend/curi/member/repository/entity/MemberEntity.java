package com.backend.curi.member.repository.entity;


import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@MappedSuperclass
public abstract class MemberEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspaceId")
    protected Workspace workspace;

    protected String name;

    protected String email;

    protected String phoneNum;

    protected LocalDate startDate;

    public void modifyInformation(String name, String email, String phoneNum, LocalDate startDate) {
        this.name = name;
        this.email = email;
        this.phoneNum = phoneNum;
        this.startDate = startDate;
    }
}
