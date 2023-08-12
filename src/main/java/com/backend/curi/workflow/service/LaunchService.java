package com.backend.curi.workflow.service;

import com.backend.curi.common.feign.SchedulerOpenFeign;
import com.backend.curi.common.feign.dto.SequenceMessageRequest;
import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.launched.repository.entity.LaunchedModule;
import com.backend.curi.launched.service.LaunchedModuleService;
import com.backend.curi.launched.repository.entity.LaunchedSequence;
import com.backend.curi.launched.service.LaunchedSequenceService;
import com.backend.curi.launched.controller.dto.LaunchedWorkflowResponse;
import com.backend.curi.launched.repository.entity.LaunchedStatus;
import com.backend.curi.launched.repository.entity.LaunchedWorkflow;
import com.backend.curi.launched.service.LaunchedWorkflowService;
import com.backend.curi.member.repository.entity.Member;
import com.backend.curi.member.service.MemberService;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.smtp.AwsSMTPService;
import com.backend.curi.workflow.controller.dto.LaunchRequest;
import com.backend.curi.workflow.repository.ContentRepository;
import com.backend.curi.workflow.repository.entity.Sequence;
import com.backend.curi.workflow.repository.entity.Module;

import com.backend.curi.workspace.repository.entity.Workspace;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class LaunchService {

    private final LaunchedWorkflowService launchedWorkflowService;
    private final LaunchedSequenceService launchedSequenceService;
    private final LaunchedModuleService launchedModuleService;
    private final WorkflowService workflowService;
    private final MemberService memberService;
    private final WorkspaceService workspaceService;

    private final AwsSMTPService awsSMTPService;

    private final ContentRepository contentRepository;

    private final SchedulerOpenFeign schedulerOpenFeign;

    @Transactional
    public LaunchedWorkflowResponse launchWorkflow(Long workflowId, LaunchRequest launchRequest, Long workspaceId){
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        var workflow = workflowService.getWorkflowEntity(workflowId);
        var currentUser = (CurrentUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var member = memberService.getMemberEntity(launchRequest.getMemberId(), currentUser);
        var launchedWorkflow = LaunchedWorkflow.of(launchRequest, workflow, member, workspace);

        var sequences = workflowService.getSequencesWithDayoffset(workflowId);
        for (var sequenceWithDayoffset : sequences){
            launchSequence(launchedWorkflow, sequenceWithDayoffset.getKey(), workspace, member, sequenceWithDayoffset.getValue());
        }

        return launchedWorkflowService.saveLaunchedWorkflow(launchedWorkflow);
    }

    private void launchSequence(LaunchedWorkflow launchedWorkflow, Sequence sequence, Workspace workspace, Member member, Integer dayOffset){
        var role = sequence.getRole();
        var assignedMember = memberService.getManagerByEmployeeAndRole(member, role);
        var launchedSequence = LaunchedSequence.of(sequence, launchedWorkflow, assignedMember, workspace, dayOffset);

        var sequenceModules = sequence.getSequenceModules();
        for (var sequenceModule : sequenceModules) {
            var module = sequenceModule.getModule();
            var order = sequenceModule.getOrderNum();
            launchModule(launchedSequence, module, workspace, Long.valueOf(order));
        }

        launchedSequenceService.saveLaunchedSequence(launchedSequence);
        var request = SequenceMessageRequest.builder()
                .id(launchedSequence.getId())
                .applyDate(launchedSequence.getUpdatedDate())
                .build();
        var response = schedulerOpenFeign.createMessage(request);
        if(response.getStatusCode() != HttpStatus.CREATED)
            throw new CuriException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorType.NETWORK_ERROR);
    }

    private void launchModule(LaunchedSequence launchedSequence, Module module, Workspace workspace, Long order){
        var launchedModule = LaunchedModule.of(module, launchedSequence, workspace, order);
        launchedModuleService.saveLaunchedModule(launchedModule);
    }

    @Transactional
    public void sendLaunchedSequenceNotification(Long launchedSequenceId){
        var launchedSequence = launchedSequenceService.getLaunchedSequenceEntity(launchedSequenceId);
        if(launchedSequence.getStatus() != LaunchedStatus.NEW)
            return;

        launchedSequence.setStatus(LaunchedStatus.IN_PROGRESS);

        var launchedWorkflow = launchedSequence.getLauchedWorkflow();
        launchedWorkflow.setStatus(LaunchedStatus.IN_PROGRESS);

        var launchedModules = launchedSequence.getLaunchedModules();
        // 모듈의 맨 첫번 째는 notification이라고 가정
        var notification = launchedModules.get(0);
        notification.setStatus(LaunchedStatus.IN_PROGRESS);

        var memberTo = launchedSequence.getMember().getEmail();
        var contentsId = notification.getContentId();
//        var contents = contentRepository.findById(contentsId)
//                .orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.CONTENT_NOT_EXISTS));
        awsSMTPService.send("test", "this is test", memberTo);

        var response = schedulerOpenFeign.deleteMessage(launchedSequenceId);
        if(response.getStatusCode() != HttpStatus.NO_CONTENT)
            throw new CuriException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorType.NETWORK_ERROR);

    }

}
