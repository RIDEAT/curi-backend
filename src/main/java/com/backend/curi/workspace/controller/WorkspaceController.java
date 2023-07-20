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
    private static Logger log = LogManager.getLogger(WorkspaceController.class.getName());

    @GetMapping("/workspace/{workspaceId}")
    @Operation(summary = "get", description = "유저의 모든 워크스페이스를 반환합니다.")
    public ResponseEntity<WorkspaceResponse> get(@PathVariable Long workspaceId, Authentication authentication) {
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        var response = workspaceService.getWorkspaceById(workspaceId);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/workspaces")
    @Operation(summary = "get List", description = "유저의 모든 워크스페이스를 반환합니다.")
    public ResponseEntity<List<WorkspaceResponse>> getList(Authentication authentication) {
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        var workspaceList = workspaceService.getWorkspaceList(currentUser);
        var resonseList = workspaceList.stream().map(WorkspaceResponse::of).collect(Collectors.toList());

        return ResponseEntity.ok(resonseList);
    }


    @PostMapping(path = "/workspace",consumes = {"application/json", "application/xml", "application/x-www-form-urlencoded"})
    @Operation(summary = "create workspace", description = "workspace 를 생성합니다.")
    public ResponseEntity createWorkspace(@RequestBody @Valid WorkspaceRequest request, Authentication authentication) {

        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        log.info("current User Id {} make workspace {}", currentUser.getUserId(), request.getName());

        var response = workspaceService.createWorkspace(request, currentUser);

        //userdb , workspace db 에 둘다 추가해줘야 합니다.
        //userId 에 대한 정보를 authentication 에서 얻어야 한다.
        //바디에 userId, workspaceId 를 담아서 리턴합니다.
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("creator", currentUser);
        responseBody.put("workspace", response);


        return new ResponseEntity(responseBody, HttpStatus.CREATED);
    }

    @PutMapping(
            path ="/workspace/{workspaceId}",
            consumes = {"application/json", "application/xml", "application/x-www-form-urlencoded"})
    @Operation(summary = "update workspace", description = "workspace 를 변경합니다.")
    public ResponseEntity updateWorkspace(@PathVariable Long workspaceId, @RequestBody @Valid WorkspaceRequest reqeust, Authentication authentication) {

        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        log.info("User {} is updating workspace {}", currentUser.getUserId(), workspaceId);

        // ... 다른 업데이트할 필드들 설정
        var response = workspaceService.updateWorkspace(workspaceId, currentUser, reqeust);

        // 업데이트된 작업 공간 정보 반환
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("transactionId", 1134);
        responseBody.put("workspace", response);
        // responseBody.put("createDate", existingWorkspace.getCreateDate());
        responseBody.put("creator", currentUser);

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }


    @DeleteMapping("workspace/{workspaceId}")
    @Operation(summary = "delete workspace", description = "workspace 를 삭제합니다.")
    public ResponseEntity deleteWorkspace(@PathVariable Long workspaceId, Authentication authentication) {
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        var response = workspaceService.deleteWorkspace(workspaceId, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
