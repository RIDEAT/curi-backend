package com.backend.curi.launched.repository.entity;

import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.frontoffice.repository.entity.FrontOffice;
import com.backend.curi.launched.controller.dto.LaunchedSequenceRequest;
import com.backend.curi.member.repository.entity.Member;
import com.backend.curi.workflow.controller.dto.SequenceResponse;
import com.backend.curi.workflow.repository.entity.Sequence;
import com.backend.curi.workflow.repository.entity.SequenceSatisfaction;
import com.backend.curi.workspace.repository.entity.Role;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LaunchedSequence extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Setter
    @Builder.Default
    @ColumnDefault("false")
    private Boolean isScored = false;

    @Setter
    @ColumnDefault("true")
    private Boolean checkSatisfaction;

    private LaunchedStatus status;

    @Setter
    @Builder.Default
    private LocalDate applyDate = LocalDate.of(2024,1,1);

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "satisfactionId")
    @Setter
    private SequenceSatisfaction sequenceSatisfaction;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membersId")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RoleId")
    private Role role;

/*
    @OneToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "LaunchedSequenceId")
    private LaunchedSequence nextSequence;
*/
    @Setter
    @OneToOne(mappedBy = "launchedSequence", cascade = CascadeType.ALL)
    private FrontOffice frontOffice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LaunchedWorkflowId")
    private LaunchedWorkflow lauchedWorkflow;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspaceId")
    private Workspace workspace;

    @OneToMany(mappedBy = "launchedSequence", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LaunchedModule> launchedModules = new ArrayList<>();

    public static LaunchedSequence of (LaunchedSequenceRequest launchedSequenceRequest/*, Employee employee, Workflow workflow, Workspace workspace*/){
         return LaunchedSequence.builder().name(launchedSequenceRequest.getName()).status(launchedSequenceRequest.getStatus())
                .applyDate(launchedSequenceRequest.getApplyDate())/*.employee(employee).workflow(workflow).workspace(workspace)*/.build();
     }

     public static LaunchedSequence of (Sequence sequence, LaunchedWorkflow launchedWorkflow, Member member, Workspace workspace){
        return LaunchedSequence.builder().name(sequence.getName()).status(LaunchedStatus.TO_DO)
                .lauchedWorkflow(launchedWorkflow).role(sequence.getRole())
                .member(member).workspace(workspace)
                .checkSatisfaction(sequence.getCheckSatisfaction()).
                applyDate(launchedWorkflow.getKeyDate().plusDays(sequence.getDayOffset())).build();
     }

     public void modify(LaunchedSequenceRequest launchedSequenceRequest){
        this.name = launchedSequenceRequest.getName();
        this.status = launchedSequenceRequest.getStatus();
        this.applyDate = launchedSequenceRequest.getApplyDate();
     }
     public void setStatus(LaunchedStatus status){
        this.status = status;
     }
}