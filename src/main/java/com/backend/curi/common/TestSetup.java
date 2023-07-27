package com.backend.curi.common;

import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.service.UserService;
import com.backend.curi.workspace.controller.dto.WorkspaceRequest;
import com.backend.curi.workspace.repository.WorkspaceRepository;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TestSetup {

    private final UserService userService;
    private final WorkspaceService workspaceService;


    private final String userId = "floN3PYjxbQ9E3MQJmiHhwDxBwb2";
    private final String userEmail = "8514199@gmail.com";
    private final String workspaceName = "rideat";
    private final String workspaceEmail = "rideat@gmail.com";


    @PostConstruct
    public void setup() {
        userService.dbStore(userId, userEmail);
        workspaceService.createWorkspace(getWorkspaceRequest(), getCurrentUser());

    }


    private WorkspaceRequest getWorkspaceRequest(){
        return new WorkspaceRequest(workspaceName, workspaceEmail);
    }


    private CurrentUser getCurrentUser(){
        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserId(userId);
        currentUser.setUserEmail(userEmail);


        // 여기에 security context 인증 정보 넣어야 할지도 .
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(currentUser, null, List.of(new SimpleGrantedAuthority(("USER"))));


       return currentUser;
    }

}
