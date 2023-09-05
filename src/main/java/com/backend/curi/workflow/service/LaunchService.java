package com.backend.curi.workflow.service;

import com.backend.curi.common.Common;
import com.backend.curi.common.feign.SchedulerOpenFeign;
import com.backend.curi.common.feign.dto.SequenceMessageRequest;
import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.frontoffice.service.FrontOfficeService;
import com.backend.curi.launched.controller.dto.LaunchedWorkflowsResponse;
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
import com.backend.curi.message.service.MessageService;
import com.backend.curi.workflow.controller.dto.*;
import com.backend.curi.workflow.repository.SequenceSatisfactionRepository;
import com.backend.curi.workflow.repository.entity.Sequence;
import com.backend.curi.workflow.repository.entity.Module;

import com.backend.curi.workflow.repository.entity.SequenceSatisfaction;
import com.backend.curi.workspace.controller.dto.RoleResponse;
import com.backend.curi.workspace.repository.entity.Role;
import com.backend.curi.workspace.repository.entity.Workspace;
import com.backend.curi.workspace.service.RoleService;
import com.backend.curi.workspace.service.WorkspaceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private final FrontOfficeService frontofficeService;
    private final MessageService messageService;
    private final SequenceSatisfactionRepository satisfactionRepository;


    private final ContentService contentService;

    private final RoleService roleService;

    private final SchedulerOpenFeign schedulerOpenFeign;


    @Transactional
    public LaunchedWorkflowsResponse launchWorkflows(Long workflowId, List<LaunchRequest> launchRequests, Long workspaceId) throws JsonProcessingException {
        var launchedWorkflows = launchRequests.stream().map(request-> {
            try {
                return launchWorkflow(workflowId, request, workspaceId);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        return LaunchedWorkflowsResponse.of(launchedWorkflows);
    }

    @Transactional
    public LaunchedWorkflowResponse launchWorkflow(Long workflowId, LaunchRequest launchRequest, Long workspaceId) throws JsonProcessingException {
        Map<Role, Member> memberMap = new HashMap<>();
        Map<Role, Member> managerMap = new HashMap<>();

        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        var workflow = workflowService.getWorkflowEntity(workflowId);
        var employeeRole = workspace.getRoles().get(0);
        var member = memberService.getMember(launchRequest.getMemberId());
        var launchedWorkflow = LaunchedWorkflow.of(launchRequest, workflow, member, workspace);

        memberMap.put(employeeRole, member);
        for (MemberRoleRequest members : launchRequest.getMembers()){
            Member manager = memberService.getMember(members.getMemberId());
            Role role = roleService.getRoleEntity(members.getRoleId());
            memberMap.put(role, manager);
            managerMap.put(role, manager);
        }

        var sequences = workflow.getSequences();
        for (var sequence : sequences) {
            launchSequence(launchedWorkflow, sequence, workspace, member, memberMap);
        }

        var response = launchedWorkflowService.saveLaunchedWorkflow(launchedWorkflow, managerMap);
        messageService.sendWorkflowLaunchedMessage(launchedWorkflow, memberMap);


        return response;
    }

    private void launchSequence(LaunchedWorkflow launchedWorkflow, Sequence sequence, Workspace workspace, Member member, Map<Role, Member> memberMap) throws JsonProcessingException {
        var role = sequence.getRole();
        Member assignedMember = memberMap.get(role);
        var launchedSequence = LaunchedSequence.of(sequence, launchedWorkflow, assignedMember, workspace);
        var satisfaction = SequenceSatisfaction.isNone(launchedSequence, member,workspace);
        satisfactionRepository.save(satisfaction);
        launchedSequence.setSequenceSatisfaction(satisfaction);

        var modules = sequence.getModules();
        for (var module : modules) {
            launchModule(launchedSequence, module, workspace, memberMap);
        }

        launchedSequenceService.saveLaunchedSequence(launchedSequence);
        launchedWorkflow.getLaunchedSequences().add(launchedSequence);

        frontofficeService.createFrontOffice(launchedSequence);

        var request = SequenceMessageRequest.builder()
                .id(launchedSequence.getId())
                .applyDate(launchedSequence.getApplyDate().atStartOfDay())
                .build();
        var response = schedulerOpenFeign.createMessage(request);
        if (response.getStatusCode() != HttpStatus.CREATED)
            throw new CuriException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorType.NETWORK_ERROR);
    }

    private void launchModule(LaunchedSequence launchedSequence, Module module, Workspace workspace,Map<Role, Member> memberMap) throws JsonProcessingException {

        var contentToCopy = contentService.getContent(module.getContentId());

        log.info(contentToCopy.toString());

        var contents = contentService.copyContents(contentToCopy);
        var launchedModule = LaunchedModule.of(contents.getId(), module, launchedSequence, workspace);

        launchedModuleService.saveLaunchedModule(launchedModule);

        launchedSequence.getLaunchedModules().add(launchedModule);
    }

    @Transactional
    public void sendLaunchedSequenceNotification(Long launchedSequenceId) {
        var launchedSequence = launchedSequenceService.getLaunchedSequenceEntity(launchedSequenceId);
        if (launchedSequence.getStatus() != LaunchedStatus.TO_DO)
            return;

        launchedSequence.setStatus(LaunchedStatus.IN_PROGRESS);

        var launchedWorkflow = launchedSequence.getLauchedWorkflow();
        launchedWorkflow.setStatus(LaunchedStatus.IN_PROGRESS);

        var memberTo = launchedSequence.getMember().getEmail();
        var frontOffice = frontofficeService.getFrontOfficeByLaunchedSequenceId(launchedSequenceId);

        messageService.sendLaunchedSequenceMessage(memberTo, frontOffice, launchedSequence);

        var response = schedulerOpenFeign.deleteMessage(launchedSequenceId);
        if (response.getStatusCode() != HttpStatus.NO_CONTENT)
            throw new CuriException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorType.NETWORK_ERROR);

    }

    private JsonNode replaceTextInNode(JsonNode node, Map<Role, Member> memberMap ) {
        ObjectMapper mapper = new ObjectMapper();
        if (node.isTextual()) {
            String text = node.asText();
            Pattern pattern = Pattern.compile("\\{(.+?)\\}");
            Matcher matcher = pattern.matcher(text);
            StringBuffer sb = new StringBuffer();

            while (matcher.find()) {
                String key = matcher.group(1);
                for (Map.Entry<Role, Member> entry : memberMap.entrySet()) {
                    if (entry.getKey().getName().equals(key)) {
                        matcher.appendReplacement(sb, entry.getValue().getName());
                        break;
                    }
                }
            }

            matcher.appendTail(sb);
            return mapper.valueToTree(sb.toString());

        } else if (node.isArray()) {
            ArrayNode arrayNode = mapper.createArrayNode();
            for (JsonNode element : node) {
                arrayNode.add(replaceTextInNode(element, memberMap));
            }
            return arrayNode;

        } else if (node.isObject()) {
            ObjectNode objectNode = mapper.createObjectNode();
            for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();
                objectNode.set(entry.getKey(), replaceTextInNode(entry.getValue(), memberMap));
            }
            return objectNode;
        }

        return node;
    }

    public RequiredForLaunchResponse getRequiredForLaunch(Long workflowId) {
        RequiredForLaunchResponse response = new RequiredForLaunchResponse();
        List<RoleResponse> requiredRoles = getRequiredRoles(workflowId);
        response.setRequiredRoles(requiredRoles);
        return response;
    }

    private List<RoleResponse> getRequiredRoles(Long workflowId) {
        List<SequenceResponse> sequenceResponses = workflowService.getSequences(workflowId);

        return sequenceResponses.stream()
                .map(SequenceResponse::getRole)
                .distinct()  // 중복 요소 제거
                .collect(Collectors.toList());
    }


}
