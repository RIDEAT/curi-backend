package com.backend.curi.member.repository.entity;

import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.member.controller.dto.MemberRequest;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.*;

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

    @Setter
    private String name;

    @Setter
    private String email;

    @Setter
    private String phoneNum;

    @Setter
    private String department;

    @Setter
    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    private MemberType type;

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

    public static Member of(MemberRequest request, Workspace workspace) {
        return Member.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNum(request.getPhoneNum())
                .type(request.getType())
                .department(request.getDepartment())
                .workspace(workspace)
                .startDate(LocalDate.parse(request.getStartDate()))
                .build();
    }
    public void modifyInformation(MemberRequest request) {
        this.name = request.getName();
        this.email = request.getEmail();
        this.phoneNum = request.getPhoneNum();
        this.department = request.getDepartment();
        this.startDate = LocalDate.parse(request.getStartDate());
    }
}
