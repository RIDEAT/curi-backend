package com.backend.curi.workflow.repository.entity;

import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.workflow.controller.dto.WorkflowRequest;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Builder
public class Workflow extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WorkflowSequence> workflowSequences = new ArrayList<>();


    public void modify(WorkflowRequest request){
        this.name = request.getName();
    }
    


}
