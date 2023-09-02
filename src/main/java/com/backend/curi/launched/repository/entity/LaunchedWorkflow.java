package com.backend.curi.launched.repository.entity;

import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.launched.controller.dto.LaunchedWorkflowRequest;
import com.backend.curi.member.repository.entity.Member;
import com.backend.curi.workflow.controller.dto.LaunchRequest;
import com.backend.curi.workflow.repository.entity.Workflow;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.*;


import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LaunchedWorkflow extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Setter
    private LaunchedStatus status;

    @Setter
    private LocalDate keyDate;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membersId")
    private Member member;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflowId")
    private Workflow workflow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspaceId")
    private Workspace workspace;

    @OneToMany(mappedBy = "launchedWorkflow", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LaunchedWorkflowManager> launchedWorkflowManagers = new ArrayList<>();

    @OneToMany(mappedBy = "lauchedWorkflow", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LaunchedSequence> launchedSequences = new ArrayList<>();

    

    public static LaunchedWorkflow of (LaunchedWorkflowRequest launchedWorkflowRequest/*, Employee employee, Workflow workflow, Workspace workspace*/){
        return LaunchedWorkflow.builder().name(launchedWorkflowRequest.getName()).status(launchedWorkflowRequest.getStatus())
                .keyDate(launchedWorkflowRequest.getKeyDate())/*.employee(employee).workflow(workflow).workspace(workspace)*/.build();
    }

    public static LaunchedWorkflow of (LaunchRequest launchRequest, Workflow workflow, Member member, Workspace workspace) {
        return LaunchedWorkflow.builder().name(workflow.getName()).status(LaunchedStatus.TO_DO).keyDate(launchRequest.getKeyDate()).member(member).workflow(workflow).workspace(workspace).build();

    }

    public void modify(LaunchedWorkflowRequest launchedWorkflowRequest){
        this.name = launchedWorkflowRequest.getName();
        this.status = launchedWorkflowRequest.getStatus();
        this.keyDate = launchedWorkflowRequest.getKeyDate();
    }


}
