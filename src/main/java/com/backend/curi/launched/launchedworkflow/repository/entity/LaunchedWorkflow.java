package com.backend.curi.launched.launchedworkflow.repository.entity;

import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.launched.launchedworkflow.controller.dto.LaunchedWorkflowRequest;
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
public class LaunchedWorkflow extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LaunchedStatus status;

    private LocalDate keyDate;

    /*
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeId")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflowId")
    private Workflow workflow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;
*/

    public static LaunchedWorkflow of (LaunchedWorkflowRequest launchedWorkflowRequest/*, Employee employee, Workflow workflow, Workspace workspace*/){
        return LaunchedWorkflow.builder().name(launchedWorkflowRequest.getName()).status(LaunchedStatus.valueOf(launchedWorkflowRequest.getStatus()))
                .keyDate(LocalDate.parse(launchedWorkflowRequest.getKeyDate()))/*.employee(employee).workflow(workflow).workspace(workspace)*/.build();
    }


}
