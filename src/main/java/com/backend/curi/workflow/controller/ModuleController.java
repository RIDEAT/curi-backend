package com.backend.curi.workflow.controller;

import com.backend.curi.exception.sequence.ValidationSequence;
import com.backend.curi.workflow.controller.dto.ModuleRequest;
import com.backend.curi.workflow.controller.dto.ModuleResponse;
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
    @Hidden
    @GetMapping("modules")
    public ResponseEntity<List<ModuleResponse>> getModules(@PathVariable Long workspaceId) {
        var response = moduleService.getModules(workspaceId);
        return ResponseEntity.ok(response);
    }

    @Hidden
    @GetMapping("modules/{moduleId}")
    public ResponseEntity<ModuleResponse> getModule(@PathVariable Long workspaceId, @PathVariable Long moduleId) {
        var response = moduleService.getModule(workspaceId, moduleId);
        return ResponseEntity.ok(response);
    }
    @Hidden
    @PostMapping("modules")
    public ResponseEntity<ModuleResponse> createModule(
            @RequestBody @Validated(ValidationSequence.class) ModuleRequest request,
            @PathVariable Long workspaceId) {
        var response = moduleService.createModule(workspaceId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ModuleResponse.of(response));
    }

    @PostMapping("workflows/{workflowId}/sequences/{sequenceId}/modules")
    public ResponseEntity<ModuleResponse> createModule(
            @RequestBody @Validated(ValidationSequence.class) ModuleRequest request,
            @PathVariable Long workspaceId,
            @PathVariable Long workflowId,
            @PathVariable Long sequenceId) {
        var response = moduleService.createModule(workspaceId, sequenceId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Hidden
    @PutMapping("modules/{moduleId}")
    public ResponseEntity<Void> modifyModule(
            @RequestBody @Validated(ValidationSequence.class) ModuleRequest request,
            @PathVariable Long workspaceId,
            @PathVariable Long moduleId) {
        moduleService.modifyModule(moduleId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("workflows/{workflowId}/sequences/{sequenceId}/modules/{moduleId}")
    public ResponseEntity<Void> modifyModule(
            @RequestBody @Validated(ValidationSequence.class) ModuleRequest request,
            @PathVariable Long workspaceId,
            @PathVariable Long workflowId,
            @PathVariable Long sequenceId,
            @PathVariable Long moduleId) {
        moduleService.modifyModule(sequenceId, moduleId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Hidden
    @DeleteMapping("modules/{moduleId}")
    public ResponseEntity<Void> deleteModule(@PathVariable Long workspaceId, @PathVariable Long moduleId) {
        moduleService.deleteModule(moduleId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("workflows/{workflowId}/sequences/{sequenceId}/modules/{moduleId}")
    public ResponseEntity<Void> deleteSequenceModule(@PathVariable Long workspaceId, @PathVariable Long workflowId,
                                                     @PathVariable Long sequenceId, @PathVariable Long moduleId) {
        moduleService.deleteSequenceModule(sequenceId, moduleId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
