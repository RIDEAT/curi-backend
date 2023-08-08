package com.backend.curi.launched.launchedsequence.repository.entity;

import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.launched.launchedmodule.repository.entity.LaunchedModule;
import com.backend.curi.launched.launchedsequence.controller.dto.LaunchedSequenceRequest;
import com.backend.curi.launched.launchedworkflow.repository.entity.LaunchedStatus;
import com.backend.curi.launched.launchedworkflow.repository.entity.LaunchedWorkflow;
import com.backend.curi.member.repository.entity.Member;
import com.backend.curi.workflow.repository.entity.Sequence;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private LaunchedStatus status;

    @Builder.Default
    private LocalDate applyDate = LocalDate.of(2000,10,9);



    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

/*
    @OneToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "LaunchedSequenceId")
    private LaunchedSequence nextSequence;
*/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LaunchedWorkflowId")
    private LaunchedWorkflow lauchedWorkflow;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @OneToMany(mappedBy = "launchedSequence", cascade = CascadeType.ALL)
    private List<LaunchedModule> launchedModules;

    public static LaunchedSequence of (LaunchedSequenceRequest launchedSequenceRequest/*, Employee employee, Workflow workflow, Workspace workspace*/){
         return LaunchedSequence.builder().name(launchedSequenceRequest.getName()).status(launchedSequenceRequest.getStatus())
                .applyDate(launchedSequenceRequest.getApplyDate())/*.employee(employee).workflow(workflow).workspace(workspace)*/.build();
     }

     public static LaunchedSequence of (Sequence sequence, LaunchedWorkflow launchedWorkflow, Member member, Workspace workspace, Integer dayOffset){
        return LaunchedSequence.builder().name(sequence.getName()).status(LaunchedStatus.NEW).lauchedWorkflow(launchedWorkflow).member(member).workspace(workspace).
                applyDate(launchedWorkflow.getKeyDate().plusDays(dayOffset)).build();
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