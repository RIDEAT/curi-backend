package com.backend.curi.member;

import com.backend.curi.common.Constants;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.service.UserService;
import com.backend.curi.workflow.service.WorkflowService;
import com.backend.curi.workspace.controller.dto.WorkspaceRequest;
import com.backend.curi.workspace.controller.dto.WorkspaceResponse;
import com.backend.curi.workspace.service.WorkspaceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import io.restassured.RestAssured;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberAcceptanceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private WorkspaceService workspaceService;

    @Autowired
    private WorkflowService workflowService;

    @LocalServerPort
    public int port;
    private final String userId = Constants.userId;
    private final String userEmail = Constants.userEmail;
    private final String workspaceName = Constants.workspaceName;
    private final String workspaceEmail = Constants.workspaceEmail;
    private final String workflowName = Constants.workflowName;
    private final String authToken = Constants.authToken;

    private Long workspaceId;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;

        userService.dbStore(userId, userEmail);
        WorkspaceResponse workspace = workspaceService.createWorkspace(getWorkspaceRequest(), getCurrentUser());
        workspaceId = workspace.getId();
    }



    /*
    @Test
    public void CreateWorkflow() {
        WorkflowRequest workflowRequest = getWorkflowRequest();
        CurrentUser currentUser = getCurrentUser();

        WorkflowResponse workflowResponse = workflowService.createWorkflow(currentUser, workspaceId, workflowRequest);

        assertNotNull(workflowResponse.getId());
        assertEquals(workflowRequest.getName(), workflowResponse.getName());
    }*/

    @Test
    public void GetUsers(){
        RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/users/{workspaceId}", workspaceId)
                .then()
                .log()
                .all()
                .extract();


    }



    @Test
    public void CreateMemeber(){
        RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/api/books")
                .then()
                .statusCode(200);

    }

    @Test
    public void launchWorkflow(){

    }






    private WorkspaceRequest getWorkspaceRequest(){
        return new WorkspaceRequest(workspaceName, workspaceEmail);
    }


    private CurrentUser getCurrentUser(){
        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserId(userId);
        currentUser.setUserEmail(userEmail);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(currentUser, null, List.of(new SimpleGrantedAuthority(("USER"))));

        return currentUser;
    }

    /*
    private WorkflowRequest getWorkflowRequest(){
        WorkflowRequest workflowRequest = new WorkflowRequest(1,workflowName);
        return workflowRequest;

    }*/
}
