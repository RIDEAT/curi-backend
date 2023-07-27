package com.backend.curi.workflow.controller;

import com.backend.curi.exception.sequence.ValidationSequence;
import com.backend.curi.workflow.controller.dto.ModuleRequest;
import com.backend.curi.workflow.controller.dto.ModuleResponse;
import com.backend.curi.workflow.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspaces/{workspaceId}")
public class ModuleController {
    private final ModuleService moduleService;

    @GetMapping("modules")
    public ResponseEntity<List<ModuleResponse>> getModules(@PathVariable Long workspaceId) {
        var response = moduleService.getModules(workspaceId);
        return ResponseEntity.ok(response);
    }
    @PostMapping("modules")
    public ResponseEntity<Void> createModule(
            @RequestBody @Validated(ValidationSequence.class) ModuleRequest request,
            @PathVariable Long workspaceId) {
        moduleService.createModule(workspaceId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("sequences/{sequenceId}/modules")
    public ResponseEntity<Void> createModule(
            @RequestBody @Validated(ValidationSequence.class) ModuleRequest request,
            @PathVariable Long workspaceId,
            @PathVariable Long sequenceId) {
        moduleService.createModule(workspaceId, sequenceId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("modules/{moduleId}")
    public ResponseEntity<Void> modifyModule(
            @RequestBody @Validated(ValidationSequence.class) ModuleRequest request,
            @PathVariable Long moduleId) {
        moduleService.modifyModule(moduleId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("sequences/{sequenceId}/modules/{moduleId}")
    public ResponseEntity<Void> modifyModule(
            @RequestBody @Validated(ValidationSequence.class) ModuleRequest request,
            @PathVariable Long sequenceId,
            @PathVariable Long moduleId) {
        moduleService.modifyModule(sequenceId, moduleId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("modules/{moduleId}")
    public ResponseEntity<Void> deleteModule(@PathVariable Long moduleId) {
        moduleService.deleteModule(moduleId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("sequences/{sequenceId}/modules/{moduleId}")
    public ResponseEntity<Void> deleteSequenceModule(@PathVariable Long sequenceId, @PathVariable Long moduleId) {
        moduleService.deleteSequenceModule(sequenceId, moduleId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
