package com.backend.curi.workflow.controller;

import com.backend.curi.exception.sequence.ValidationSequence;
import com.backend.curi.workflow.controller.dto.*;
import com.backend.curi.workflow.service.ModuleService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspaces/{workspaceId}")
public class ModuleController {
    private final ModuleService moduleService;

    private static Logger log = LoggerFactory.getLogger(ModuleController.class);

    @PostMapping("workflows/{workflowId}/sequences/{sequenceId}/modules")
    public ResponseEntity<ModuleResponse> createModule(
            @RequestBody @Validated(ValidationSequence.class) ModuleRequest request,
            @PathVariable Long workspaceId,
            @PathVariable Long workflowId,
            @PathVariable Long sequenceId) {
        var response = moduleService.createModule(workspaceId, sequenceId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ModuleResponse.of(response));
    }


    @PutMapping("workflows/{workflowId}/sequences/{sequenceId}/modules/{moduleId}")
    public ResponseEntity<Void> modifyModule(
            @RequestBody @Validated(ValidationSequence.class) ModuleRequest request,
            @PathVariable Long workspaceId,
            @PathVariable Long workflowId,
            @PathVariable Long sequenceId,
            @PathVariable Long moduleId) {
        moduleService.modifyModule(moduleId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("workflows/{workflowId}/sequences/{sequenceId}/modules/{moduleId}")
    public ResponseEntity<Void> updateModule(
            @RequestBody @Validated(ValidationSequence.class) ModuleUpdateRequest request,
            @PathVariable Long workspaceId,
            @PathVariable Long workflowId,
            @PathVariable Long sequenceId,
            @PathVariable Long moduleId) {
        moduleService.updateModule(moduleId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("workflows/{workflowId}/sequences/{sequenceId}/modules/{moduleId}")
    public ResponseEntity<Void> deleteSequenceModule(@PathVariable Long workspaceId, @PathVariable Long workflowId,
                                                     @PathVariable Long sequenceId, @PathVariable Long moduleId) {
        moduleService.deleteModule(moduleId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @PatchMapping("workflows/{workflowId}/sequences/{sequenceId}/modules/{moduleId}/content")
    public ResponseEntity<ContentResponse> updateContent(@RequestBody @Validated(ValidationSequence.class) ContentUpdateRequest request,
                                                      @PathVariable Long workspaceId, @PathVariable Long workflowId,
                                                      @PathVariable Long sequenceId, @PathVariable Long moduleId) {
        var response = moduleService.updateContent(moduleId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("workflows/{workflowId}/sequences/{sequenceId}/modules/{moduleId}/content")
    public ResponseEntity<ContentResponse> getContent(@PathVariable Long workspaceId, @PathVariable Long workflowId,
                                                         @PathVariable Long sequenceId, @PathVariable Long moduleId) {
        var response = moduleService.getContent(moduleId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
