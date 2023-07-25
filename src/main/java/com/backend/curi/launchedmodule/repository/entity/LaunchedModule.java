package com.backend.curi.launchedmodule.repository.entity;

import com.backend.curi.common.entity.BaseEntity;
import com.backend.curi.launchedmodule.controller.dto.LaunchedModuleRequest;
import com.backend.curi.launchedsequence.repository.entity.LaunchedSequence;
import com.backend.curi.launchedworkflow.repository.entity.LaunchedStatus;
import com.backend.curi.workspace.repository.entity.Workspace;
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
public class LaunchedModule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LaunchedStatus status;

    // private ModuleType type;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LaunchedSequenceId")
    private LaunchedSequence launchedSequence;

    private Long mongoId;

    private Long orderInSequence;

    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;
*/

    public static LaunchedModule of (LaunchedModuleRequest launchedModuleRequest, LaunchedSequence launchedSequence){
        return LaunchedModule.builder().name(launchedModuleRequest.getName()).status(launchedModuleRequest.getStatus()).launchedSequence(launchedSequence).mongoId(launchedModuleRequest.getMongoId()).orderInSequence(launchedModuleRequest.getOrder()).build();
    }

}
