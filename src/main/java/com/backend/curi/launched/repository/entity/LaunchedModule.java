package com.backend.curi.launched.repository.entity;

import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.launched.controller.dto.LaunchedModuleRequest;
import com.backend.curi.workflow.controller.dto.ModuleResponse;
import com.backend.curi.workflow.repository.entity.ModuleType;
import com.backend.curi.workflow.repository.entity.Module;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LaunchedModule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LaunchedStatus status;

    private ModuleType type;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LaunchedSequenceId")
    private LaunchedSequence launchedSequence;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "moduleId")
    private Module module;

    private ObjectId contentId;

    private Integer orderInSequence;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;


    public static LaunchedModule of (LaunchedModuleRequest launchedModuleRequest, LaunchedSequence launchedSequence){
        return LaunchedModule.builder().name(launchedModuleRequest.getName()).status(launchedModuleRequest.getStatus()).type(launchedModuleRequest.getType()).launchedSequence(launchedSequence).workspace(launchedSequence.getWorkspace()).contentId(launchedModuleRequest.getContentId()).orderInSequence(launchedModuleRequest.getOrder()).build();
    }


    public static LaunchedModule of (ObjectId launchedContentId, Module module, LaunchedSequence launchedSequence, Workspace workspace){
        return LaunchedModule.builder().name(module.getName()).status(LaunchedStatus.TO_DO).type(module.getType()).launchedSequence(launchedSequence).workspace(workspace).module(module).contentId(launchedContentId).orderInSequence(module.getOrder()).build();
    }

    public void modify (LaunchedModuleRequest request){
        this.name = request.getName();
        this.orderInSequence = request.getOrder();
    }
    public void setStatus(LaunchedStatus status){
        this.status = status;
    }

}
