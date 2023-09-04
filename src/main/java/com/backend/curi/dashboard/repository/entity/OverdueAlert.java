package com.backend.curi.dashboard.repository.entity;

import com.backend.curi.dashboard.repository.AlertStatus;
import com.backend.curi.launched.repository.entity.LaunchedSequence;
import com.backend.curi.member.repository.entity.MemberType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "overdue_alerts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class OverdueAlert {
    @Id
    private ObjectId id;
    private Long workspaceId;
    private Long memberId;
    private Long roleId;
    private Long launchedSequenceId;

    private String memberName;
    private String roleTitle;
    private String SequenceTitle;
    private String WorkflowTitle;

    private LocalDate applyDate;
    private LocalDate updatedDate;
    private AlertStatus status;
    private MemberType memberType;
    public static OverdueAlert of(LaunchedSequence sequence){
        var member = sequence.getMember();
        var role = sequence.getRole();
        var workflow = sequence.getLauchedWorkflow();
        return OverdueAlert.builder()
                .workspaceId(sequence.getWorkspace().getId())
                .memberId(member.getId())
                .roleId(role.getId())
                .launchedSequenceId(sequence.getId())
                .memberName(member.getName())
                .roleTitle(role.getName())
                .SequenceTitle(sequence.getName())
                .WorkflowTitle(workflow.getName())
                .applyDate(sequence.getApplyDate())
                .updatedDate(LocalDate.now())
                .status(AlertStatus.OVERDUE)
                .memberType(member.getType())
                .build();
    }
}
