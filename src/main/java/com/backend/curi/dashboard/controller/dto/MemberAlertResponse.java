package com.backend.curi.dashboard.controller.dto;

import com.backend.curi.dashboard.repository.AlertStatus;
import com.backend.curi.dashboard.repository.entity.OverdueAlert;
import com.backend.curi.launched.repository.entity.LaunchedSequence;
import com.backend.curi.workspace.controller.dto.RoleResponse;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@NoArgsConstructor
public class MemberAlertResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private MemberInfo memberInfo;
    private LaunchedSequenceInfo launchedSequenceInfo;
    private RoleResponse role;
    private Long overdue;
    private LocalDate updatedDate;
    private AlertStatus status;

    public static MemberAlertResponse of(OverdueAlert alert) {
        var overdue = ChronoUnit.DAYS.between(alert.getApplyDate(), LocalDate.now());
        MemberAlertResponse memberAlertResponse = new MemberAlertResponse();
        memberAlertResponse.setId(alert.getId());
        memberAlertResponse.setMemberInfo(new MemberInfo(alert.getMemberId(), alert.getMemberName()));
        memberAlertResponse.setLaunchedSequenceInfo(new LaunchedSequenceInfo(alert.getLaunchedSequenceId(), alert.getSequenceTitle(), alert.getWorkflowTitle()));
        memberAlertResponse.setRole(new RoleResponse(alert.getRoleId(), alert.getRoleTitle()));
        memberAlertResponse.setOverdue(overdue);
        memberAlertResponse.setUpdatedDate(alert.getUpdatedDate());
        memberAlertResponse.setStatus(alert.getStatus());
        return memberAlertResponse;
    }

    public static MemberAlertResponse mock(String name, String sequence, String workflow, String role, Long overdue) {
        MemberAlertResponse memberAlertResponse = new MemberAlertResponse();
        memberAlertResponse.id = new ObjectId();
        memberAlertResponse.setMemberInfo(new MemberInfo(1L, name));
        memberAlertResponse.setLaunchedSequenceInfo(new LaunchedSequenceInfo(1L, sequence, workflow));
        memberAlertResponse.setRole(new RoleResponse(1L, role));
        memberAlertResponse.setOverdue(overdue);
        memberAlertResponse.setUpdatedDate(LocalDate.now());
        memberAlertResponse.setStatus(AlertStatus.OVERDUE);
        return memberAlertResponse;
    }

    @AllArgsConstructor
    private static class MemberInfo {
        public Long id;
        public String name;
    }

    @AllArgsConstructor
    private static class LaunchedSequenceInfo {
        public Long id;
        public String title;
        public String workflowTitle;
    }
}
