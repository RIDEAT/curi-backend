package com.backend.curi.member.repository.entity;

import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.member.controller.dto.ManagerRequest;
import com.backend.curi.member.controller.dto.MemberRequest;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Manager extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "manager")
    private Member member;

    public void modify(MemberRequest request) {
        var managerRequest = (ManagerRequest) request;
    }

    public static ManagerBuilder of(MemberRequest request) {
        return Manager.builder();
    }

}
