package com.backend.curi.workflow.service;

import com.backend.curi.common.feign.SchedulerOpenFeign;
import com.backend.curi.common.feign.dto.SequenceMessageRequest;
import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.frontoffice.repository.entity.FrontOffice;
import com.backend.curi.frontoffice.service.FrontOfficeService;
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
import com.backend.curi.slack.controller.dto.SlackMessageRequest;
import com.backend.curi.smtp.AwsSMTPService;
import com.backend.curi.workflow.controller.dto.LaunchRequest;
import com.backend.curi.workflow.controller.dto.ModuleResponse;
import com.backend.curi.workflow.controller.dto.RequiredForLaunchResponse;
import com.backend.curi.workflow.controller.dto.SequenceResponse;
import com.backend.curi.workflow.repository.entity.Sequence;
import com.backend.curi.workflow.repository.entity.Module;
import com.backend.curi.slack.service.SlackService;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    private final AwsSMTPService awsSMTPService;

    private final ContentService contentService;

    private final RoleService roleService;

    private final SchedulerOpenFeign schedulerOpenFeign;

    private final SlackService slackService;



    @Transactional
    public LaunchedWorkflowResponse launchWorkflow(Long workflowId, LaunchRequest launchRequest, Long workspaceId) throws JsonProcessingException {
        Map<Role, Member> memberMap = new HashMap<>();

        var workspace = workspaceService.getWorkspaceEntityById(workspaceId);
        var workflow = workflowService.getWorkflowEntity(workflowId);
        var member = memberService.getMember(launchRequest.getMemberId());
        var launchedWorkflow = LaunchedWorkflow.of(launchRequest, workflow, member, workspace);
        List<Role> requiredRoleEntities = getRequiredRoles(workflowId).stream().map(RoleResponse -> roleService.getRoleEntity(RoleResponse.getId())).collect(Collectors.toList());

        for (Role role : requiredRoleEntities) {
            System.out.println(role.getId());
            if (role.getName().equals("신규입사자")) memberMap.put(role, member);
            else {
                Member manager = memberService.getManagerByEmployeeAndRole(member, role);
                memberMap.put(role, manager);
            }
        }


        var sequences = workflow.getSequences();
        for (var sequence : sequences) {
            launchSequence(launchedWorkflow, sequence, workspace, member, memberMap);
        }

        var response = launchedWorkflowService.saveLaunchedWorkflow(launchedWorkflow);


        sendWorkflowLaunchedMessage(launchedWorkflow, memberMap);
        //slackService.sendWorkflowLaunchedMessage(response);
        //awsSMTPService.send("test", "this is test", launchedWorkflow.getMember().getEmail());

        return response;
    }

    private void launchSequence(LaunchedWorkflow launchedWorkflow, Sequence sequence, Workspace workspace, Member member, Map<Role, Member> memberMap) throws JsonProcessingException {
        var role = sequence.getRole();
        Member assignedMember = memberMap.get(role);
        var launchedSequence = LaunchedSequence.of(sequence, launchedWorkflow, assignedMember, workspace);

        var modules = sequence.getModules();
        for (var module : modules) {
            launchModule(launchedSequence, module, workspace, memberMap);
        }

        launchedSequenceService.saveLaunchedSequence(launchedSequence);
        launchedWorkflow.getLaunchedSequences().add(launchedSequence);


        frontofficeService.createFrontOffice(launchedSequence);
        var request = SequenceMessageRequest.builder()
                .id(launchedSequence.getId())
                .applyDate(launchedSequence.getUpdatedDate())
                .build();
        var response = schedulerOpenFeign.createMessage(request);
        if (response.getStatusCode() != HttpStatus.CREATED)
            throw new CuriException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorType.NETWORK_ERROR);
    }

    private void launchModule(LaunchedSequence launchedSequence, Module module, Workspace workspace,Map<Role, Member> memberMap) throws JsonProcessingException {

        Object content = contentService.getContent(module.getContentId());

        log.info(content.toString());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(content.toString());

        JsonNode replaced = replaceTextInNode(rootNode, memberMap);

        log.info(replaced.toPrettyString());

        var contents = contentService.createContents(replaced.toPrettyString());

        var launchedModule = LaunchedModule.of(contents.getId(), module, launchedSequence, workspace);

        launchedModuleService.saveLaunchedModule(launchedModule);

        launchedSequence.getLaunchedModules().add(launchedModule);
    }

    @Transactional
    public void sendLaunchedSequenceNotification(Long launchedSequenceId) {
        var launchedSequence = launchedSequenceService.getLaunchedSequenceEntity(launchedSequenceId);
        if (launchedSequence.getStatus() != LaunchedStatus.NEW)
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

    void sendWorkflowLaunchedMessage (LaunchedWorkflow launchedWorkflow, Map<Role, Member> memberMap){
        // send to Admin user
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        log.info("send workflow launch alarm to admin");

        slackService.sendWorkflowLaunchedMessage(launchedWorkflow);
        awsSMTPService.send("workflow is launched", "this is test", currentUser.getUserEmail());

        log.info ("send workflow launch alarm to employee");

        slackService.sendWorkflowLaunchedMessageToEmployee(launchedWorkflow);
        awsSMTPService.send("workflow is launched", "this is test", launchedWorkflow.getMember().getEmail());

        log.info ("send workflow launch alarm to related managers");

        for (Map.Entry<Role, Member> entry : memberMap.entrySet()) {
            Role role = entry.getKey();
            Member member = entry.getValue();
            slackService.sendWorkflowLaunchedMessageToManagers(launchedWorkflow, role, member);
            awsSMTPService.send("workflow is launched", "this is test", member.getEmail());
        }

    }
}
