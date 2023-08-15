package com.backend.curi.workspace.controller;


import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.workspace.controller.dto.WorkspaceRequest;
import com.backend.curi.workspace.controller.dto.WorkspaceResponse;
import com.backend.curi.workspace.repository.entity.Workspace;
import com.backend.curi.workspace.service.WorkspaceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    @GetMapping("/workspaces/{workspaceId}")
    public ResponseEntity<WorkspaceResponse> getWorkspace(@PathVariable Long workspaceId, Authentication authentication) {
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
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
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        var response = workspaceService.createWorkspace(request, currentUser);

        return new ResponseEntity(response, HttpStatus.CREATED);
    }

    @PutMapping(
            path ="/workspaces/{workspaceId}",
            consumes = {"application/json", "application/xml", "application/x-www-form-urlencoded"})
    public ResponseEntity updateWorkspace(@PathVariable Long workspaceId, @RequestBody @Valid WorkspaceRequest reqeust, Authentication authentication) {

        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();

        var response = workspaceService.updateWorkspace(workspaceId, currentUser, reqeust);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @DeleteMapping("workspaces/{workspaceId}")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable Long workspaceId, Authentication authentication) {
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        workspaceService.deleteWorkspace(workspaceId, currentUser);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
