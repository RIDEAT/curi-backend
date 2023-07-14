package com.backend.curi.workspace.controller;


import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.repository.entity.User_;
import com.backend.curi.user.service.UserService;
import com.backend.curi.userworkspace.service.UserworkspaceService;
import com.backend.curi.workspace.controller.dto.WorkspaceForm;
import com.backend.curi.workspace.repository.entity.Workspace;
import com.backend.curi.workspace.service.WorkspaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/workspace")
public class WorkspaceController {

    private final UserService userService;
    private final WorkspaceService workspaceService;
    private final UserworkspaceService userworkspaceService;

    @GetMapping
    @Operation(summary = "get List", description = "유저의 모든 워크스페이스를 반환합니다.",
            parameters = {
                    @Parameter(
                            name = "refreshToken",
                            in = ParameterIn.COOKIE,
                            schema = @Schema(implementation = String.class)
                    )
            })
    @SecurityRequirement(name = "Auth-token")
    public ResponseEntity getList(Authentication authentication) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("transactionId", 11);

        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        String userId = currentUser.getUserId();
        var user = userService.getUserByUserId(userId);
        List<Workspace> workspaceList = userworkspaceService.getWorkspaceListByUser(user);

        responseBody.put("list", workspaceList);

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }


    @PostMapping(consumes = {"application/json", "application/xml", "application/x-www-form-urlencoded"})
    @Operation(summary = "create workspace", description = "workspace 를 생성합니다.",
            parameters = {
                    @Parameter(
                            name = "refreshToken",
                            in = ParameterIn.COOKIE,
                            schema = @Schema(implementation = String.class)
                    )
            })
    @SecurityRequirement(name = "Auth-token")
    public ResponseEntity createWorkspace(@RequestBody @Valid WorkspaceForm workspaceForm, Authentication authentication) {

        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        log.info("current User Id {} make workspace {}", currentUser.getUserId(), workspaceForm.getName());

        var user = userService.getUserByUserId(currentUser.getUserId());
        var workspace = workspaceService.createWorkspace(workspaceForm, currentUser.getUserId());
        userworkspaceService.create(user, workspace);

        //userdb , workspace db 에 둘다 추가해줘야 합니다.
        //userId 에 대한 정보를 authentication 에서 얻어야 한다.
        //바디에 userId, workspaceId 를 담아서 리턴합니다.
        Map<String, Object> responseBody = new HashMap<>();

        responseBody.put("creator", user);
        responseBody.put("workspace", workspace);


        return new ResponseEntity(responseBody, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{workspaceId}",
            consumes = {"application/json", "application/xml", "application/x-www-form-urlencoded"})
    @Operation(summary = "update workspace", description = "workspace 를 변경합니다.",
            parameters = {
                    @Parameter(
                            name = "refreshToken",
                            in = ParameterIn.COOKIE,
                            schema = @Schema(implementation = String.class)
                    )
            })
    @SecurityRequirement(name = "Auth-token")
    public ResponseEntity updateWorkspace(@PathVariable int workspaceId, @RequestBody @Valid WorkspaceForm workspaceForm, Authentication authentication) {


        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        log.info("User {} is updating workspace {}", currentUser.getUserId(), workspaceId);

        var user = userService.getUserByUserId(currentUser.getUserId());
        var workspace = workspaceService.getWorkspaceById(workspaceId);

        // 수정 권한이 있는 사람만 확인하는 로직
        if (!userworkspaceService.exist(user, workspace)) {
            throw new CuriException(HttpStatus.FORBIDDEN, ErrorType.UNAUTHORIZED_WORKSPACE);
        }


        // 업데이트할 작업 공간 정보 설정
        workspace.setName(workspaceForm.getName());
        workspace.setEmail(workspaceForm.getEmail());
        // ... 다른 업데이트할 필드들 설정

        workspaceService.updateWorkspace(workspace);

        // 업데이트된 작업 공간 정보 반환
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("transactionId", 1134);
        responseBody.put("name", workspace.getName());
        responseBody.put("id", workspace.getWorkspaceId());
        responseBody.put("emailId", workspace.getEmail());
        // responseBody.put("createDate", existingWorkspace.getCreateDate());
        responseBody.put("creator", currentUser);

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }


    @DeleteMapping("/{workspaceId}")
    @Operation(summary = "delete workspace", description = "workspace 를 삭제합니다.",
            parameters = {
                    @Parameter(
                            name = "refreshToken",
                            in = ParameterIn.COOKIE,
                            schema = @Schema(implementation = String.class)
                    )
            })
    @SecurityRequirement(name = "Auth-token")
    public ResponseEntity deleteWorkspace(@PathVariable int workspaceId, Authentication authentication) {

        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();

        var user = userService.getUserByUserId(currentUser.getUserId());
        var workspace = workspaceService.getWorkspaceById(workspaceId);

        // 수정 권한이 있는 사람만 확인하는 로직
        if (!userworkspaceService.exist(user, workspace)) {
            throw new CuriException(HttpStatus.FORBIDDEN, ErrorType.UNAUTHORIZED_WORKSPACE);
        }

        log.info("User {} is deleting workspace {}", currentUser.getUserId(), workspaceId);

        // 작업 공간 삭제 로직 수행
        workspaceService.deleteWorkspace(workspaceId, currentUser.getUserId());
        userworkspaceService.delete(user, workspace);

        // 삭제 성공 응답 반환
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Workspace deleted successfully");
        responseBody.put("workspaceId", workspaceId);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    private List<Workspace> convertToWorkspace(List<Integer> workspaceIdList) {
        List<Workspace> workspaceList = new ArrayList<>();
        for (Integer workspaceId : workspaceIdList) {
            workspaceList.add(workspaceService.getWorkspaceById(workspaceId));
        }
        return workspaceList;
    }


}
