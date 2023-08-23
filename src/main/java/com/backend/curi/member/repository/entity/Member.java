package com.backend.curi.member.repository.entity;

import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.member.controller.dto.EmployeeRequest;
import com.backend.curi.member.controller.dto.ManagerRequest;
import com.backend.curi.member.controller.dto.MemberRequest;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "members")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspaceId")
    private Workspace workspace;

    private String name;

    private String email;

    private String phoneNum;

    private String department;

    @Enumerated(EnumType.STRING)
    private MemberType type;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "employeeId")
    private Employee employee;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "managerId")
    private Manager manager;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static MemberBuilder of(MemberRequest request) {
        return Member.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNum(request.getPhoneNum())
                .department(request.getDepartment());
    }
    public void modifyInformation(MemberRequest request) {
        this.name = request.getName();
        this.email = request.getEmail();
        this.phoneNum = request.getPhoneNum();
        this.department = request.getDepartment();

        if(type == MemberType.employee) {
            getEmployee().modify(request);
        } else {
            this.manager.modify(request);
        }
    }

    public List<EmployeeManager> getEmployeeManagers() {
        if(type == MemberType.employee) {
            return getEmployee().getEmployeeManagers();
        } else {
            return this.manager.getEmployeeManagers();
        }
    }
}
