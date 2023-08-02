package com.backend.curi.workflow.service;

import com.backend.curi.launched.launchedmodule.repository.entity.LaunchedModule;
import com.backend.curi.launched.launchedmodule.service.LaunchedModuleService;
import com.backend.curi.launched.launchedsequence.repository.entity.LaunchedSequence;
import com.backend.curi.launched.launchedsequence.service.LaunchedSequenceService;
import com.backend.curi.launched.launchedworkflow.repository.entity.LaunchedWorkflow;
import com.backend.curi.launched.launchedworkflow.service.LaunchedWorkflowService;
import com.backend.curi.member.repository.entity.EmployeeManager;
import com.backend.curi.member.repository.entity.Member;
import com.backend.curi.member.service.MemberService;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.workflow.controller.dto.LaunchRequest;
import com.backend.curi.workflow.controller.dto.SequenceResponse;
import com.backend.curi.workflow.repository.entity.SequenceModule;
import com.backend.curi.workflow.repository.entity.Module;

import com.backend.curi.workflow.repository.entity.WorkflowSequence;
import com.backend.curi.workspace.repository.entity.Workspace;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaunchService {

    private final LaunchedWorkflowService launchedWorkflowService;
    private final LaunchedSequenceService launchedSequenceService;
    private final LaunchedModuleService launchedModuleService;
    private final WorkflowService workflowService;
    private final SequenceService sequenceService;
    private final ModuleService moduleService;
    private final MemberService memberService;
    private final WorkspaceService workspaceService;

    @Transactional
    public void launchWorkflow(Long workflowId, LaunchRequest launchRequest, Long workspaceId){
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        var workflow = workflowService.getWorkflowEntity(workflowId);
        var currentUser = (CurrentUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var member = memberService.getMemberEntity(launchRequest.getMemberId(), currentUser);
        var launchedWorkflow = LaunchedWorkflow.of(launchRequest, workflow, member, workspace);


        var sequenceResposneList = workflowService.getSequencesWithDayoffset(workflowId);
        for (SimpleEntry<SequenceResponse, Integer> sequenceResponseWithDayoffset : sequenceResposneList){
            launchSequence(launchedWorkflow, sequenceResponseWithDayoffset.getKey().getId(), workspace, member, sequenceResponseWithDayoffset.getValue());
        }

        launchedWorkflowService.saveLaunchedWorkflow(launchedWorkflow);
    }

    private void launchSequence(LaunchedWorkflow launchedWorkflow, Long sequenceId, Workspace workspace, Member member, Integer dayOffset){
        var sequence = sequenceService.getSequenceEntity(sequenceId);
        var role = sequence.getRole();
        var assignedMember = memberService.getManagerByEmployeeAndRole(member, role);
        var launchedSequence = LaunchedSequence.of(sequence, launchedWorkflow, assignedMember, workspace, dayOffset);

        List<SequenceModule> sequenceModuleList = sequence.getSequenceModules();

        for (SequenceModule sequenceModule : sequenceModuleList) {
            var module = sequenceModule.getModule();
            var order = sequenceModule.getOrderNum();
            launchModule(launchedSequence, module, workspace, Long.valueOf(order));
        }

        launchedSequenceService.saveLaunchedSequence(launchedSequence);
    }

    private void launchModule(LaunchedSequence launchedSequence, Module module, Workspace workspace, Long order){

        var launchedModule = LaunchedModule.of(module, launchedSequence, workspace, order);
        launchedModuleService.saveLaunchedModule(launchedModule);
    }

}
