package com.backend.curi.workSpace.controller;


import com.backend.curi.exception.CuriException;
import com.backend.curi.user.service.UserService;
import com.backend.curi.workSpace.controller.dto.WorkSpaceForm;
import com.backend.curi.workSpace.service.WorkSpaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/workSpace")
public class WorkSpaceController {

    private final WorkSpaceService workSpaceService;
    private final UserService userService;



    @PostMapping("/create")
    public ResponseEntity createWorkSpace (@RequestBody @Valid WorkSpaceForm workSpaceForm, Authentication authentication){
        try{
            //workspaceForm 에 대한 유효성 검사 필요함
            int workSpaceId = workSpaceService.createWorkSpace(workSpaceForm);

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

    @GetMapping("/{workSpaceId}")
    public ResponseEntity getWorkSpaceName(@PathVariable int workSpaceId){
        try {
            String workSpaceName = workSpaceService.getWorkSpaceNameByWorkSpaceId(workSpaceId);
            return new ResponseEntity(HttpStatus.ACCEPTED);

        } catch (CuriException e){
            log.error(e.getMessage());
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }

    }
}
