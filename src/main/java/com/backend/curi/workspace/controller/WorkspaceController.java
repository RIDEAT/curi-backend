package com.backend.curi.workspace.controller;


import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.security.dto.CurrentUser;
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
import org.springframework.validation.BindingResult;
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
    public ResponseEntity getList ( Authentication authentication){
        try {
            if (authentication == null) {
                throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS);
            }
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("transactionId", 11);

            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            String userId = currentUser.getUserId();

            Map<String, Object> user = new HashMap<>();
            user.put("id", userId);
            responseBody.put("user", user);

            List<Integer> workspaceIdList = userworkspaceService.getWorkspaceIdListByUserId(userId);
            if (workspaceIdList.isEmpty()) throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKSPACE_NOT_EXISTS);


            responseBody.put("list", convertToWorkspace(workspaceIdList));

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (CuriException e) {
            log.error(e.getMessage());
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("error", e.getMessage());

            return new ResponseEntity<>(errorBody, e.getHttpStatus());
        }
    }



    @PostMapping(consumes = { "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Operation(summary = "create workspace", description = "workspace 를 생성합니다.",
            parameters = {
                    @Parameter(
                            name = "refreshToken",
                            in = ParameterIn.COOKIE,
                            schema = @Schema(implementation = String.class)
                    )
            })
    @SecurityRequirement(name = "Auth-token")
    public ResponseEntity createWorkspace (@RequestBody @Valid WorkspaceForm workspaceForm, BindingResult bindingResult, Authentication authentication){
        try{
            if (authentication == null) throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS);

            //workspaceForm 에 대한 유효성 검사
            if (bindingResult.hasErrors()) {
                // 유효성 검사 실패한 필드 및 에러 메시지 확인
                List<FieldError> fieldErrors = bindingResult.getFieldErrors();
                Map<String, Object> errorBody = new HashMap<>();
                errorBody.put("error", "폼이 유효하지 않습니다.");
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
                return new ResponseEntity<>(errorBody, responseHeaders, HttpStatus.BAD_REQUEST);
            }

            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            log.info("current User Id {} make workspace {}", currentUser.getUserId(), workspaceForm.getName());

            int workspaceId = workspaceService.createWorkspace(workspaceForm, currentUser.getUserId());
            userworkspaceService.create (currentUser.getUserId(), workspaceId);

            //userdb , workspace db 에 둘다 추가해줘야 합니다.
            //userId 에 대한 정보를 authentication 에서 얻어야 한다.
            //바디에 userId, workspaceId 를 담아서 리턴합니다.
            Map<String, Object> responseBody = new HashMap<>();

            Map<String, Object> creator = new HashMap<>();
            creator.put("id", currentUser.getUserId());

            Map<String, Object> workspace = new HashMap<>();
            workspace.put("id", workspaceId);
            workspace.put("name", workspaceForm.getName());

            responseBody.put("creator", creator);
            responseBody.put("workspace", workspace);


            return new ResponseEntity(responseBody,HttpStatus.CREATED);

        } catch (CuriException e){
            log.error(e.getMessage());
            Map<String, Object> errorBody= new HashMap<>();
            errorBody.put("error", e.getMessage());

            return new ResponseEntity(errorBody, e.getHttpStatus());
        } catch (HttpClientErrorException e){
            log.info(e.getMessage());
            Map<String, Object> errorBody= new HashMap<>();
            errorBody.put("error", e.getMessage());

            return new ResponseEntity(errorBody, e.getStatusCode());
        }

    }

    @PutMapping(value = "/{workspaceId}",
            consumes = { "application/json", "application/xml", "application/x-www-form-urlencoded" })
    @Operation(summary = "update workspace", description = "workspace 를 변경합니다.",
            parameters = {
                    @Parameter(
                            name = "refreshToken",
                            in = ParameterIn.COOKIE,
                            schema = @Schema(implementation = String.class)
                    )
            })
    @SecurityRequirement(name = "Auth-token")
    public ResponseEntity updateWorkspace(@PathVariable int workspaceId, @RequestBody @Valid WorkspaceForm workspaceForm, BindingResult bindingResult, Authentication authentication) {
        try {
            if (authentication == null) {
                throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS);
            }

            //workspaceForm 에 대한 유효성 검사
            if (bindingResult.hasErrors()) {
                // 유효성 검사 실패한 필드 및 에러 메시지 확인
                List<FieldError> fieldErrors = bindingResult.getFieldErrors();
                Map<String, Object> errorBody = new HashMap<>();
                errorBody.put("error", "폼이 유효하지 않습니다.");
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
                return new ResponseEntity<>(errorBody, responseHeaders, HttpStatus.BAD_REQUEST);
            }

            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            log.info("User {} is updating workspace {}", currentUser.getUserId(), workspaceId);


            // 수정 권한이 있는 사람만 확인하는 로직
            if (!userworkspaceService.exist(currentUser.getUserId(), workspaceId)) {
                throw new CuriException(HttpStatus.FORBIDDEN, ErrorType.UNAUTHORIZED_WORKSPACE);
            }


            Workspace existingWorkspace = workspaceService.getWorkspaceById(workspaceId);


            // 업데이트할 작업 공간 정보 설정
            existingWorkspace.setName(workspaceForm.getName());
            existingWorkspace.setEmail(workspaceForm.getEmail());
            // ... 다른 업데이트할 필드들 설정

            workspaceService.updateWorkspace(existingWorkspace);

            // 업데이트된 작업 공간 정보 반환
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("transactionId", 1134);
            responseBody.put("name", existingWorkspace.getName());
            responseBody.put("id", existingWorkspace.getWorkspaceId());
            responseBody.put("emailId", existingWorkspace.getEmail());
           // responseBody.put("createDate", existingWorkspace.getCreateDate());
            responseBody.put("creator", currentUser);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (CuriException e) {
            log.error(e.getMessage());
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("error", e.getMessage());
            return new ResponseEntity<>(errorBody, e.getHttpStatus());
        }
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
        try {
            if (authentication == null) {
                throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS);
            }

            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();

            // 수정 권한이 있는 사람만 확인하는 로직
            if (!userworkspaceService.exist(currentUser.getUserId(), workspaceId)) {
                throw new CuriException(HttpStatus.FORBIDDEN, ErrorType.UNAUTHORIZED_WORKSPACE);
            }

            log.info("User {} is deleting workspace {}", currentUser.getUserId(), workspaceId);

            // 작업 공간 삭제 로직 수행
            workspaceService.deleteWorkspace(workspaceId, currentUser.getUserId());
            userworkspaceService.delete(currentUser.getUserId(), workspaceId);

            // 삭제 성공 응답 반환
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Workspace deleted successfully");
            responseBody.put("workspaceId", workspaceId);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (CuriException e) {
            log.error(e.getMessage());
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("error", e.getMessage());
            return new ResponseEntity<>(errorBody, e.getHttpStatus());
        }
    }

    private List<Workspace> convertToWorkspace(List<Integer> workspaceIdList){
        List<Workspace> workspaceList = new ArrayList<>();
        for (Integer workspaceId : workspaceIdList){
            workspaceList.add(workspaceService.getWorkspaceById(workspaceId));
        }
        return workspaceList;
    }




}
