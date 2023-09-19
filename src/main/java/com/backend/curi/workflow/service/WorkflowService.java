package com.backend.curi.workflow.service;

import java.util.AbstractMap.SimpleEntry;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.slack.controller.dto.SlackMessageRequest;
import com.backend.curi.slack.service.SlackService;
import com.backend.curi.workflow.controller.dto.ChatbotResponse;
import com.backend.curi.workflow.controller.dto.SequenceResponse;
import com.backend.curi.workflow.controller.dto.WorkflowRequest;
import com.backend.curi.workflow.controller.dto.WorkflowResponse;
import com.backend.curi.workflow.repository.SequenceRepository;
import com.backend.curi.workflow.repository.WorkflowRepository;
import com.backend.curi.workflow.repository.entity.ModuleType;
import com.backend.curi.workflow.repository.entity.Sequence;
import com.backend.curi.workflow.repository.entity.Workflow;
import com.backend.curi.workspace.repository.WorkspaceRepository;
import com.backend.curi.workspace.repository.entity.Workspace;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import org.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
@RequiredArgsConstructor
public class WorkflowService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkflowRepository workflowRepository;
    private final SlackService slackService;
    private final SequenceRepository sequenceRepository;
    private final ContentService contentService;
    private static Logger log = LoggerFactory.getLogger(WorkflowService.class);

    @Transactional
    public WorkflowResponse createWorkflow(Long workspaceId, WorkflowRequest request) {
        var workspace = getWorkspaceEntityById(workspaceId);

        var workflow = Workflow.builder()
                .name(request.getName())
                .workspace(workspace)
                .build();
        workflowRepository.save(workflow);
        createDefaultSequence(workspace, workflow);
        slackService.sendMessageToRideat(new SlackMessageRequest("새로운 워크플로우가 생성되었습니다. 이름 : " + request.getName() + ", 워크스페이스 : " + workspace.getId()));

        return WorkflowResponse.of(workflow);
    }

    @Transactional
    public Workflow copyWorkflow(Workspace workspace, Workflow origin) {
        var workflow = Workflow.builder()
                .name(origin.getName())
                .workspace(workspace)
                .build();
        workflowRepository.save(workflow);
        return workflow;
    }

    public WorkflowResponse getWorkflow(Long workflowId) {
        var workflow = getWorkflowEntity(workflowId);

        return WorkflowResponse.of(workflow);
    }

    @Transactional
    public List<WorkflowResponse> getWorkflows(Long workspaceId) {
        var workspace = getWorkspaceEntityById(workspaceId);
        var workflowList = workflowRepository.findAllByWorkspace(workspace);
        return workflowList.stream().map(WorkflowResponse::of).collect(Collectors.toList());
    }

    @Transactional
    public WorkflowResponse updateWorkflow(Long workflowId, WorkflowRequest request) {
        var workflow = getWorkflowEntity(workflowId);
        workflow.modify(request);
        return WorkflowResponse.of(workflow);
    }

    public void deleteWorkflow(Long workflowId) {
        var workflow = getWorkflowEntity(workflowId);
        workflowRepository.delete(workflow);
    }

    public List<SequenceResponse> getSequences(Long workflowId) {
        var workflow = getWorkflowEntity(workflowId);
        var sequenceList = workflow.getSequences();
        sequenceList.sort((o1, o2) -> o1.getDayOffset().compareTo(o2.getDayOffset()));
        return sequenceList.stream().map(SequenceResponse::of).collect(Collectors.toList());
    }

    public Workflow getWorkflowEntity(Long workflowId) {
        return workflowRepository.findById(workflowId)
                .orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKFLOW_NOT_EXISTS));
    }


    @Transactional
    public void createDefaultSequence(Workspace workspace, Workflow workflow) {
        var role = workspace.getRoles().get(0);
        var sequence = Sequence.builder()
                .name("기본 시퀀스")
                .role(role)
                .workspace(workspace)
                .workflow(workflow)
                .dayOffset(0)
                .build();
        sequenceRepository.save(sequence);
    }

    private Workspace getWorkspaceEntityById(Long workspaceId) {
        return workspaceRepository.findById(workspaceId).orElseThrow(() -> new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKSPACE_NOT_EXISTS));
    }

    public String allText(Long workflowId) {
        try {


            String text = "";
            var workflow = getWorkflowEntity(workflowId);
            var sequences = workflow.getSequences();
            for (Sequence sequence : sequences) {
                var modules = sequence.getModules();
                for (var module : modules) {
                    log.info(module.getName());
                    log.info(module.getType().toString());
                    if (module.getType().equals(ModuleType.contents)) {
                        JSONObject jsonObject = new JSONObject(contentService.getContent(module.getContentId()).getContent());


                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode jsonNode = objectMapper.readTree(jsonObject.toString());
                        List<String> textStartsWithList = new ArrayList<>();
                        extractTextStartsWith(jsonNode, "text", textStartsWithList);
                        for (String textStartsWith : textStartsWithList) {
                            text += textStartsWith;
                            text += ",";
                        }

                        log.info(text);


                        //여기 text 에 이상한거들어있다.
                    }
                }
            }

            return text;
        } catch (Exception e) {
            log.info(e.toString());
            return "error";
        }
    }

    private static void extractTextStartsWith(JsonNode node, String prefix, List<String> result) {
        if (node.isObject()) {
            Iterator<String> fieldNames = node.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode fieldValue = node.get(fieldName);
                log.info(fieldValue.toString());
                if (fieldName.equals(prefix) && fieldValue.isTextual()) {
                    String text = fieldValue.asText();

                    Pattern pattern = Pattern.compile("[0-9a-zA-Z가-힣\\s]+");
                    Matcher matcher = pattern.matcher(text);
                    while (matcher.find()) {
                        result.add(matcher.group());
                    }                }
                extractTextStartsWith(fieldValue, prefix, result);
            }
        } else if (node.isArray()) {
            for (JsonNode element : node) {
                extractTextStartsWith(element, prefix, result);
            }
        }
    }
}
