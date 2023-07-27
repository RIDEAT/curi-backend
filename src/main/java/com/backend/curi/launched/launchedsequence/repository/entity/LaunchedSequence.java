package com.backend.curi.launched.launchedsequence.repository.entity;

import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.launched.launchedsequence.controller.dto.LaunchedSequenceRequest;
import com.backend.curi.launched.launchedworkflow.repository.entity.LaunchedStatus;
import com.backend.curi.launched.launchedworkflow.repository.entity.LaunchedWorkflow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
*/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LaunchedWorkflowId")
    private LaunchedWorkflow lauchedWorkflow;

/*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;
*/

    public static LaunchedSequence of (LaunchedSequenceRequest launchedSequenceRequest/*, Employee employee, Workflow workflow, Workspace workspace*/){
         return LaunchedSequence.builder().name(launchedSequenceRequest.getName()).status(launchedSequenceRequest.getStatus())
                .applyDate(LocalDate.parse(launchedSequenceRequest.getApplyDate())).orderInWorkflow(launchedSequenceRequest.getOrder())/*.employee(employee).workflow(workflow).workspace(workspace)*/.build();
     }

}