package com.backend.curi.member.repository.entity;

import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class Manager extends MemberEntity {

    private String department;

    public void modifyInformation(String name, String email, String phoneNum, LocalDate startDate, String department) {
        super.modifyInformation(name, email, phoneNum, startDate);
        this.department = department;
    }

    @Builder
    public Manager(Long id,
                   Workspace workspace,
                   String name,
                   String email,
                   String phoneNum,
                   LocalDate startDate,
                   String department) {
        this.id = id;
        this.workspace = workspace;
        this.name = name;
        this.email = email;
        this.phoneNum = phoneNum;
        this.startDate = startDate;
        this.department = department;
    }
}
