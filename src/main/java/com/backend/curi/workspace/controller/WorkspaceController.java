package com.backend.curi.workspace.controller;


import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.service.UserService;
import com.backend.curi.workspace.controller.dto.WorkspaceForm;
import com.backend.curi.workspace.repository.entity.Workspace;
import com.backend.curi.workspace.service.WorkspaceService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/workspace")
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity getList ( Authentication authentication){
        try {
            if (authentication == null) {
                throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS);
            }
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("transactionId", 11);

            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            String userId = currentUser.getUserId();
            List<Workspace> workspaces = workspaceService.getWorkspacesByUserId(userId);
            if (workspaces.isEmpty()) throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKSPACE_NOT_EXISTS);
            responseBody.put("list", workspaces);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (CuriException e) {
            log.error(e.getMessage());
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("error", e.getMessage());

            return new ResponseEntity<>(errorBody, HttpStatus.NOT_ACCEPTABLE);
        }
    }



    @PostMapping
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

            //userdb , workspace db 에 둘다 추가해줘야 합니다.
            //userId 에 대한 정보를 authentication 에서 얻어야 한다.
            //바디에 userId, workspaceId 를 담아서 리턴합니다.
            Map<String, Object> responseBody= new HashMap<>();

            responseBody.put("userId", currentUser.getUserId());
            responseBody.put("workspaceId", workspaceId);
            responseBody.put("workspaceName", workspaceForm.getName());



            return new ResponseEntity(responseBody,HttpStatus.ACCEPTED);

        } catch (CuriException e){
            log.error(e.getMessage());
            Map<String, Object> errorBody= new HashMap<>();
            errorBody.put("error", e.getMessage());

            return new ResponseEntity(errorBody, HttpStatus.NOT_ACCEPTABLE);
        } catch (HttpClientErrorException e){
            log.info(e.getMessage());
            Map<String, Object> errorBody= new HashMap<>();
            errorBody.put("error", e.getMessage());

            return new ResponseEntity(errorBody, HttpStatus.NOT_ACCEPTABLE);
        }

    }

    @PutMapping("/{workspaceId}")
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

            Workspace existingWorkspace = workspaceService.getWorkspaceById(workspaceId);

            // 업데이트할 작업 공간 정보 설정
            existingWorkspace.setName(workspaceForm.getName());
            existingWorkspace.setEmail(workspaceForm.getEmail());
            // ... 다른 업데이트할 필드들 설정

            workspaceService.updateWorkspace(existingWorkspace);

            // 업데이트된 작업 공간 정보 반환
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("transactionId", 1134);
            responseBody.put("workspaceName", existingWorkspace.getName());
            responseBody.put("workspaceId", existingWorkspace.getWorkspaceId());
            responseBody.put("emailId", existingWorkspace.getEmail());
           // responseBody.put("createDate", existingWorkspace.getCreateDate());
            responseBody.put("creator", currentUser);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (CuriException e) {
            log.error(e.getMessage());
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("error", e.getMessage());
            return new ResponseEntity<>(errorBody, HttpStatus.NOT_ACCEPTABLE);
        }
    }


}
