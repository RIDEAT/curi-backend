package com.backend.curi.dashboard.controller.dto;

import com.backend.curi.launched.repository.entity.LaunchedSequence;
import com.backend.curi.workspace.controller.dto.RoleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
@NoArgsConstructor
public class MemberAlertResponse {

    private MemberInfo memberInfo;
    private LaunchedSequenceInfo launchedSequenceInfo;
    private RoleResponse role;
    private Integer overdue;

    public static MemberAlertResponse of(LaunchedSequence launchedSequence){
        var member = launchedSequence.getMember();
        var workflow = launchedSequence.getSequence().getWorkflow();
        var role = RoleResponse.of(launchedSequence.getRole());
        var overdue = Period.between(LocalDate.now(), launchedSequence.getApplyDate()).getDays();
        MemberAlertResponse memberAlertResponse = new MemberAlertResponse();
        memberAlertResponse.setMemberInfo(new MemberInfo(member.getId(), member.getName()));
        memberAlertResponse.setLaunchedSequenceInfo(new LaunchedSequenceInfo(launchedSequence.getId(), launchedSequence.getName(), workflow.getName()));
        memberAlertResponse.setRole(role);
        memberAlertResponse.setOverdue(overdue);
        return memberAlertResponse;
    }

    public static MemberAlertResponse mock(String name, String sequence, String workflow, String role, Integer overdue){
        MemberAlertResponse memberAlertResponse = new MemberAlertResponse();
        memberAlertResponse.setMemberInfo(new MemberInfo(1L, name));
        memberAlertResponse.setLaunchedSequenceInfo(new LaunchedSequenceInfo(1L, sequence, workflow));
        memberAlertResponse.setRole(new RoleResponse(1L, role));
        memberAlertResponse.setOverdue(overdue);
        return memberAlertResponse;
    }

    @AllArgsConstructor
    private static class MemberInfo{
        public Long id;
        public String name;
    }

    @AllArgsConstructor
    private static class LaunchedSequenceInfo{
        public Long id;
        public String title;
        public String workflowTitle;
    }
}
