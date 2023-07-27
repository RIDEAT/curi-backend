package com.backend.curi.launched.launchedmodule.controller;

import com.backend.curi.launched.launchedmodule.controller.dto.LaunchedModuleRequest;
import com.backend.curi.launched.launchedmodule.controller.dto.LaunchedModuleResponse;
import com.backend.curi.launched.launchedmodule.service.LaunchedModuleService;
import lombok.RequiredArgsConstructor;
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

}
