package com.backend.curi.workspace.controller;


import com.backend.curi.exception.CuriException;
import com.backend.curi.user.service.UserService;
import com.backend.curi.workspace.controller.dto.WorkspaceForm;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/workSpace")
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final UserService userService;



    @PostMapping("/create")
    public ResponseEntity createWorkSpace (@RequestBody @Valid WorkspaceForm workSpaceForm, Authentication authentication, HttpServletResponse response){
        try{
            //workspaceForm 에 대한 유효성 검사 필요함
            int workSpaceId = workspaceService.createWorkspace(workSpaceForm);

            String userId = authentication.getPrincipal().toString();
            userService.createWorkspace(userId, workSpaceId);

            //userdb , workspace db 에 둘다 추가해줘야 합니다.
            //userId 에 대한 정보를 authentication 에서 얻어야 한다.

            return new ResponseEntity(HttpStatus.ACCEPTED);

        } catch (CuriException e){
            log.error(e.getMessage());
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }

    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity getWorkspaceName(@PathVariable int workspaceId){
        try {
            String workspaceName = workspaceService.getWorkSpaceNameByWorkspaceId(workspaceId);
            return new ResponseEntity(HttpStatus.ACCEPTED);

        } catch (CuriException e){
            log.error(e.getMessage());
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }

    }
}
