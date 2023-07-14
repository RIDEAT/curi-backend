package com.backend.curi.member.repository.entity;


import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Employee extends MemberEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Manager manager;

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    @Builder
    public Employee(Long id,
                    Workspace workspace,
                    String name,
                    String email,
                    String phoneNum,
                    LocalDate startDate,
                    Manager manager) {
        this.id= id;
        this.workspace = workspace;
        this.name = name;
        this.email = email;
        this.phoneNum = phoneNum;
        this.startDate = startDate;
        this.manager = manager;
    }
}
