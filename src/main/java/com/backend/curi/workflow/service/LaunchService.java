package com.backend.curi.workflow.service;

import com.backend.curi.common.configuration.LoggingAspect;
import com.backend.curi.common.feign.SchedulerOpenFeign;
import com.backend.curi.common.feign.dto.SequenceMessageRequest;
import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.frontoffice.repository.entity.Frontoffice;
import com.backend.curi.frontoffice.service.FrontofficeService;
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
import com.backend.curi.workflow.controller.dto.RequiredForLaunchResponse;
import com.backend.curi.workflow.controller.dto.SequenceResponse;
import com.backend.curi.workflow.repository.entity.Sequence;
import com.backend.curi.workflow.repository.entity.Module;

import com.backend.curi.workspace.controller.dto.RoleResponse;
import com.backend.curi.workspace.repository.entity.Role;
import com.backend.curi.workspace.repository.entity.Workspace;
import com.backend.curi.workspace.service.RoleService;
import com.backend.curi.workspace.service.WorkspaceService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaunchService {

    private static Logger log = LoggerFactory.getLogger(LaunchService.class);


    private final LaunchedWorkflowService launchedWorkflowService;
    private final LaunchedSequenceService launchedSequenceService;
    private final LaunchedModuleService launchedModuleService;
    private final WorkflowService workflowService;
    private final MemberService memberService;
    private final WorkspaceService workspaceService;
    private final FrontofficeService frontofficeService;

    private final AwsSMTPService awsSMTPService;

    private final ContentService contentService;

    private final RoleService roleService;

    private final SchedulerOpenFeign schedulerOpenFeign;

    private Map<Role, Member> memberMap= new HashMap<>();
    @Transactional
    public LaunchedWorkflowResponse launchWorkflow(Long workflowId, LaunchRequest launchRequest, Long workspaceId){
        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        var workflow = workflowService.getWorkflowEntity(workflowId);
        var currentUser = (CurrentUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var member = memberService.getMemberEntity(launchRequest.getMemberId(), currentUser);
        var launchedWorkflow = LaunchedWorkflow.of(launchRequest, workflow, member, workspace);
        List<Role> requiredRoleEntities = getRequiredRoles(workflowId).stream().map(RoleResponse -> roleService.getRoleEntity(RoleResponse.getId())).collect(Collectors.toList());

        for (Role role : requiredRoleEntities){

            if (role.getName().equals("신규입사자")) memberMap.put(role, member);
            else {
                Member manager = memberService.getManagerByEmployeeAndRole(member, role);
                memberMap.put(role, manager);
            }
        }

        var sequences = workflowService.getSequencesWithDayoffset(workflowId);
        for (var sequenceWithDayoffset : sequences){
            launchSequence(launchedWorkflow, sequenceWithDayoffset.getKey(), workspace, member, sequenceWithDayoffset.getValue());
        }

        return launchedWorkflowService.saveLaunchedWorkflow(launchedWorkflow);
    }

    private void launchSequence(LaunchedWorkflow launchedWorkflow, Sequence sequence, Workspace workspace, Member member, Integer dayOffset){
        var role = sequence.getRole();
        Member assignedMember = memberMap.get(role);

        var launchedSequence = LaunchedSequence.of(sequence, launchedWorkflow, assignedMember, workspace, dayOffset);


        var sequenceModules = sequence.getSequenceModules();
        for (var sequenceModule : sequenceModules) {
            var module = sequenceModule.getModule();
            var order = sequenceModule.getOrderNum();
            launchModule(launchedSequence, module, workspace, Long.valueOf(order));
        }

        launchedSequenceService.saveLaunchedSequence(launchedSequence);
        frontofficeService.createFrontoffice(launchedSequence);

        var request = SequenceMessageRequest.builder()
                .id(launchedSequence.getId())
                .applyDate(launchedSequence.getUpdatedDate())
                .build();
        var response = schedulerOpenFeign.createMessage(request);
        if(response.getStatusCode() != HttpStatus.CREATED)
            throw new CuriException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorType.NETWORK_ERROR);
    }

    private void launchModule(LaunchedSequence launchedSequence, Module module, Workspace workspace, Long order){

        String message = contentService.getMessage(module.getContentId()).toString();

        log.info(message);

        Object substitutedMessage = substitutePlaceholders(message);

        log.info(substitutedMessage.toString());

        var content = contentService.createContent(substitutedMessage);

        var launchedModule = LaunchedModule.of(content.getId(), module, launchedSequence, workspace, order);

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

    public String substitutePlaceholders(String jsonString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            if (jsonNode.isObject()) {
                JsonNode contentNode = jsonNode.get("content");
                if (contentNode != null && contentNode.isTextual()) {
                    String content = contentNode.textValue();
                    for (Map.Entry<Role, Member> entry : memberMap.entrySet()) {
                        String placeholder = "{" + entry.getKey().getName() + "}";
                        content = content.replace(placeholder, entry.getValue().getName());
                    }
                    ((com.fasterxml.jackson.databind.node.ObjectNode) jsonNode).put("content", content);
                }
            }

            return jsonNode.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public RequiredForLaunchResponse getRequiredForLaunch(Long workflowId) {
        RequiredForLaunchResponse response = new RequiredForLaunchResponse();
        List<RoleResponse> requiredRoles = getRequiredRoles(workflowId);
        response.setRequiredRoles(requiredRoles);
        return response;
    }

    private List<RoleResponse> getRequiredRoles (Long workflowId){
        List<SequenceResponse> sequenceResponses = workflowService.getSequences(workflowId);

        return sequenceResponses.stream()
                .map(SequenceResponse::getRole)
                .distinct()  // 중복 요소 제거
                .collect(Collectors.toList());
    }
}
