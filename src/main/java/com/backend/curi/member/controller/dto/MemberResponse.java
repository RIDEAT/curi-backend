package com.backend.curi.member.controller.dto;

import com.backend.curi.member.repository.entity.Member;
import com.backend.curi.member.repository.entity.MemberType;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MemberResponse {

    private Long id;

    private Long wid;

    private String name;

    private String phoneNum;

    private String email;

    private String department;

    private MemberType type;

    private LocalDate startDate;

    public static MemberResponse of(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getWorkspace().getId(),
                member.getName(),
                member.getPhoneNum(),
                member.getEmail(),
                member.getDepartment(),
                member.getType(),
                member.getStartDate());
    }
}
