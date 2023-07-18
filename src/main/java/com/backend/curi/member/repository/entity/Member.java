package com.backend.curi.member.repository.entity;

import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspaceId")
    protected Workspace workspace;

    protected String name;

    protected String email;

    protected String phoneNum;

    protected String department;

    protected LocalDate startDate;

    @Enumerated(EnumType.STRING)
    protected MemberType type;
    
}
