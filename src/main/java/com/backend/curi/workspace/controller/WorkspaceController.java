package com.backend.curi.workspace.controller;


import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.smtp.AwsS3Service;
import com.backend.curi.workflow.controller.dto.WorkflowResponse;
import com.backend.curi.workspace.controller.dto.LogoPreSignedUrlResponse;
import com.backend.curi.workspace.controller.dto.LogoSignedUrlResponse;
import com.backend.curi.workspace.controller.dto.WorkspaceRequest;
import com.backend.curi.workspace.controller.dto.WorkspaceResponse;
import com.backend.curi.workspace.repository.entity.Workspace;
import com.backend.curi.workspace.service.WorkspaceService;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @GetMapping("/workspaces/{workspaceId}")
    public ResponseEntity<WorkspaceResponse> getWorkspace(@PathVariable Long workspaceId) {
        var response = workspaceService.getWorkspaceById(workspaceId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/workspaces")
    public ResponseEntity<List<WorkspaceResponse>> getWorkspaces(Authentication authentication) {
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        var workspaceList = workspaceService.getWorkspaceList(currentUser);
        var resonseList = workspaceList.stream().map(WorkspaceResponse::of).collect(Collectors.toList());

        return ResponseEntity.ok(resonseList);
    }


    @PostMapping(path = "/workspaces",consumes = {"application/json", "application/xml", "application/x-www-form-urlencoded"})
    public ResponseEntity<WorkspaceResponse> createWorkspace(@RequestBody @Valid WorkspaceRequest request, Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = workspaceService.createWorkspace(request, currentUser);

        return new ResponseEntity(response, HttpStatus.CREATED);
    }

    @PutMapping(
            path ="/workspaces/{workspaceId}",
            consumes = {"application/json", "application/xml", "application/x-www-form-urlencoded"})
    public ResponseEntity<WorkspaceResponse> updateWorkspace(@PathVariable Long workspaceId, @RequestBody @Valid WorkspaceRequest reqeust) {
        var response = workspaceService.updateWorkspace(workspaceId, reqeust);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @DeleteMapping("workspaces/{workspaceId}")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable Long workspaceId, Authentication authentication) {
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        workspaceService.deleteWorkspace(workspaceId, currentUser);

        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @GetMapping("workspaces/{workspaceId}/logo")
    public ResponseEntity<LogoSignedUrlResponse> getWorkspaceLogo(@PathVariable Long workspaceId){
        return new ResponseEntity<>(workspaceService.getWorkspaceLogo(workspaceId), HttpStatus.OK);
    }

    @PutMapping("workspaces/{workspaceId}/logo")
    public ResponseEntity<LogoPreSignedUrlResponse> modifyWorkspaceLogo(@PathVariable Long workspaceId, @RequestParam("fileName") String fileName) {
        return new ResponseEntity<>(workspaceService.setWorkspaceLogo(workspaceId, fileName), HttpStatus.OK);
    }
    @DeleteMapping("workspaces/{workspaceId}/logo")
    public ResponseEntity<Void> deleteWorkspaceLogo(@PathVariable Long workspaceId){
        workspaceService.deleteWorkspaceLogo(workspaceId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/templates")
    public ResponseEntity<List<WorkflowResponse>> getTemplateWorkflows() {
        return ResponseEntity.ok(workspaceService.getTemplateWorkflows());
    }

    @PostMapping("workspaces/{workspaceId}/templates/{templateId}")
    public ResponseEntity<WorkflowResponse> createTemplateWorkflows(@PathVariable Long workspaceId, @PathVariable Long templateId) {
        var response = workspaceService.copyTemplateWorkflows(workspaceId, templateId);
        return ResponseEntity.ok(response);
    }
}
