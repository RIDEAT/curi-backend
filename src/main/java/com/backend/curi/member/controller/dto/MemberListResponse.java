package com.backend.curi.member.controller.dto;

import com.backend.curi.member.repository.entity.Employee;
import com.backend.curi.member.repository.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MemberListResponse {
    private String status;
    private List<MemberResponse> memberListResponse;

    public static MemberListResponse of(List<Member> memberList) {
        var responseList = memberList.stream()
                .map(MemberResponse::of)
                .collect(Collectors.toList());
        return new MemberListResponse("success", responseList);
    }
}
