package com.backend.curi.workflow.controller;

import com.backend.curi.exception.sequence.ValidationSequence;
import com.backend.curi.launched.controller.dto.LaunchedWorkflowResponse;
import com.backend.curi.launched.controller.dto.LaunchedWorkflowsResponse;
import com.backend.curi.launched.repository.entity.LaunchedWorkflow;
import com.backend.curi.launched.service.LaunchedWorkflowService;
import com.backend.curi.workflow.controller.dto.*;
import com.backend.curi.workflow.service.LaunchService;
import com.backend.curi.workflow.service.WorkflowService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;


import org.springframework.http.converter.StringHttpMessageConverter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspaces/{workspaceId}/workflows")
public class WorkflowController {
    @Value("${workplug.ai.url}")
    private String aiUrl;
    private static Logger log = LoggerFactory.getLogger(WorkflowController.class);


    private final WorkflowService workflowService;
    private final LaunchedWorkflowService launchedWorkflowService;
    private final LaunchService launchService;



    @GetMapping("/{workflowId}/requiredforlaunch")
    public ResponseEntity<RequiredForLaunchResponse> getRequiredForLaunch(@PathVariable Long workspaceId, @PathVariable Long workflowId){
        var requiredForLaunchResponse = launchService.getRequiredForLaunch(workflowId);
        return ResponseEntity.ok(requiredForLaunchResponse);
    }


    @PostMapping("/{workflowId}/launch")
    public ResponseEntity<LaunchedWorkflowsResponse> launchWorkflow(@RequestBody @Validated(ValidationSequence.class) List<LaunchRequest> launchRequests, @PathVariable Long workspaceId, @PathVariable Long workflowId) throws JsonProcessingException {
        var launchResponse = launchService.launchWorkflows(workflowId, launchRequests, workspaceId);
        return ResponseEntity.status(HttpStatus.CREATED).body(launchResponse);
    }


    @PostMapping("/{workflowId}/chat")
    public ResponseEntity<ChatbotResponse> chatWithAi(@RequestBody @Validated(ValidationSequence.class) ChatbotRequest chatbotRequest, @PathVariable Long workflowId) throws UnsupportedEncodingException {
        String allText = workflowService.allText(workflowId);
        if (allText.length() < 10) {
            return ResponseEntity.ok(new ChatbotResponse(false, "워크플로우 내용이 너무 짧습니다. 워크플로우 내용을 추가해주세요."));
        }

        String question = chatbotRequest.getMessage();
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters()
                .stream()
                .filter(converter -> converter instanceof StringHttpMessageConverter)
                .forEach(converter -> ((StringHttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        // Jackson을 사용하여 JSON 객체 생성
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonRequest = objectMapper.createObjectNode();
        jsonRequest.put("question", question);
        jsonRequest.put("text", allText);

        // JSON 문자열로 변환
        String jsonRequestString = jsonRequest.toString();

        HttpEntity<String> entity = new HttpEntity<>(jsonRequestString, headers);
        log.info("aiUrl: {}",aiUrl);
        ResponseEntity<String> response = restTemplate.exchange(aiUrl + "/chat", HttpMethod.POST, entity, String.class);

        String responseBody = response.getBody();
        log.info("responseBody: {}", responseBody);
        return ResponseEntity.ok(new ChatbotResponse(true, responseBody));
    }


    @PostMapping
    public ResponseEntity<WorkflowResponse> createWorkflow(@RequestBody @Validated(ValidationSequence.class) WorkflowRequest request,
                                               @PathVariable Long workspaceId,
                                               Authentication authentication) {
        var response = workflowService.createWorkflow(workspaceId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping
    public ResponseEntity<List<WorkflowResponse>> getWorkflows(@PathVariable Long workspaceId) {
        var response = workflowService.getWorkflows(workspaceId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{workflowId}")
    public ResponseEntity<WorkflowResponse> getWorkflow(@PathVariable Long workspaceId, @PathVariable Long workflowId) {
        var response = workflowService.getWorkflow(workflowId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{workflowId}/sequences")
    public ResponseEntity<List<SequenceResponse>> getSequences(@PathVariable Long workflowId) {
        var response = workflowService.getSequences(workflowId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{workflowId}")
    public ResponseEntity<WorkflowResponse> updateWorkflow(@RequestBody @Validated(ValidationSequence.class) WorkflowRequest request,
                                               @PathVariable Long workflowId,
                                               Authentication authentication) {
        var response = workflowService.updateWorkflow(workflowId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{workflowId}")
    public ResponseEntity<WorkflowResponse> deleteWorkflow(@PathVariable Long workflowId,
                                               Authentication authentication) {
        workflowService.deleteWorkflow(workflowId);
        var response = new WorkflowResponse();
        response.setCreatedDate(LocalDateTime.now());
        response.setUpdatedDate(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
