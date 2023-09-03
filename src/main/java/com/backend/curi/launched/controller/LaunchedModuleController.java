package com.backend.curi.launched.controller;

import com.backend.curi.launched.controller.dto.LaunchedModuleRequest;
import com.backend.curi.launched.controller.dto.LaunchedModuleResponse;
import com.backend.curi.launched.service.LaunchedModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workspaces/{workspaceId}/launchedworkflows/{launchedworkflowId}/sequences/{sequenceId}/modules")
@RequiredArgsConstructor

public class LaunchedModuleController {

    private final LaunchedModuleService launchedModuleService;

    @GetMapping("/{moduleId}")
    public ResponseEntity<LaunchedModuleResponse> getLaunchedModule(@PathVariable Long workspaceId, @PathVariable Long launchedworkflowId, @PathVariable Long sequenceId, @PathVariable Long moduleId){
        LaunchedModuleResponse launchedModule = launchedModuleService.getLaunchedModule(moduleId);
        return ResponseEntity.ok(launchedModule);
    }

    @PostMapping
    public ResponseEntity<LaunchedModuleResponse> createLaunchedModule(@PathVariable Long workspaceId, @PathVariable Long launchedworkflowId, @PathVariable Long sequenceId, @RequestBody LaunchedModuleRequest launchedModuleRequest){
        LaunchedModuleResponse createdLaunchedModule = launchedModuleService.createLaunchedModule(launchedModuleRequest);
        return ResponseEntity.ok(createdLaunchedModule);
    }

    @PutMapping("/{moduleId}")
    public ResponseEntity<LaunchedModuleResponse> updateLaunchedModule(@PathVariable Long workspaceId, @PathVariable Long launchedworkflowId, @PathVariable Long sequenceId, @PathVariable Long moduleId, @RequestBody LaunchedModuleRequest launchedModuleRequest){
        LaunchedModuleResponse updatedLaunchedModule = launchedModuleService.updateLaunchedModule(launchedModuleRequest, moduleId);
        return ResponseEntity.ok(updatedLaunchedModule);
    }

    @DeleteMapping("/{moduleId}")
    public ResponseEntity<Void> deleteLaunchedModule (@PathVariable Long workspaceId, @PathVariable Long launchedworkflowId, @PathVariable Long sequenceId, @PathVariable Long moduleId){
        launchedModuleService.deleteLaunchedModule(moduleId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
