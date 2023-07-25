package com.backend.curi.launchedsequence.repository.entity;

import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.launchedsequence.controller.dto.LaunchedSequenceRequest;
import com.backend.curi.launchedworkflow.repository.entity.LaunchedStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

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

    private LocalDate applyDate;

    private Long orderInWorkflow;

    /*

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @OneToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "LaunchedSequenceId")
    private LaunchedSequence nextSequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LaunchedWorkflowId")
    private LaunchedWorkflow lauchedWorkflow;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;*/

    public static LaunchedSequence of (LaunchedSequenceRequest launchedSequenceRequest/*, Employee employee, Workflow workflow, Workspace workspace*/){
         return LaunchedSequence.builder().name(launchedSequenceRequest.getName()).status(LaunchedStatus.valueOf(launchedSequenceRequest.getStatus()))
                .applyDate(LocalDate.parse(launchedSequenceRequest.getApplyDate())).orderInWorkflow(launchedSequenceRequest.getOrder())/*.employee(employee).workflow(workflow).workspace(workspace)*/.build();
     }

}