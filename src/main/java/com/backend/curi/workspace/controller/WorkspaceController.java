package com.backend.curi.workspace.controller;


import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.service.UserService;
import com.backend.curi.workspace.controller.dto.WorkspaceForm;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/workspace")
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final UserService userService;



    @PostMapping("/create")
    public ResponseEntity createWorkspace (@RequestBody @Valid WorkspaceForm workspaceForm, Authentication authentication){
        try{
            if (authentication == null) throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS);

            //workspaceForm 에 대한 유효성 검사 필요함

            int workspaceId = workspaceService.createWorkspace(workspaceForm);


            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
            log.info("current User Id {} make workspace {}", currentUser.getUserId(), workspaceForm.getName());

            userService.createWorkspace(currentUser.getUserId(), workspaceId);

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
        }

    }

    @GetMapping("/enter/{workspaceId}")
    public ResponseEntity getWorkspaceName(@PathVariable int workspaceId, Authentication authentication){
        try {
            if (authentication == null) throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS);

            String workspaceName = workspaceService.getWorkSpaceNameByWorkspaceId(workspaceId);

            // workspace 의 userId 와 curretUser 의 id 확인해야함

            Map<String, Object> responseBody= new HashMap<>();
            CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();

            responseBody.put("userId", currentUser.getUserId());
            responseBody.put("workspaceId", workspaceId);
            responseBody.put("workspaceName", workspaceName);


            return new ResponseEntity(responseBody, HttpStatus.ACCEPTED);

        } catch (CuriException e){
            log.error(e.getMessage());
            Map<String, Object> errorBody= new HashMap<>();
            errorBody.put("error", e.getMessage());

            return new ResponseEntity(errorBody, HttpStatus.NOT_ACCEPTABLE);
        }

    }
}
