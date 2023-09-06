package com.backend.curi.workspace;

import com.backend.curi.common.Constants;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.service.UserService;
import com.backend.curi.workflow.service.WorkflowService;
import com.backend.curi.workspace.controller.dto.WorkspaceRequest;
import com.backend.curi.workspace.controller.dto.WorkspaceResponse;
import com.backend.curi.workspace.service.WorkspaceService;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import io.restassured.RestAssured;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-data.properties")
public class WorkspaceAcceptanceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private WorkspaceService workspaceService;

    @Autowired
    private WorkflowService workflowService;

    @LocalServerPort
    public int port;

    private final String userId = Constants.userEmail;
    private final String userName = Constants.userName;
    private final String userEmail = Constants.userEmail;
    private final String workspaceName = Constants.workspaceName;
    private final String workspaceEmail = Constants.workspaceEmail;
    private final String workflowName = Constants.workflowName;
    private final String authToken = Constants.authToken;
    private Long workspaceId;

    @BeforeEach
    public void setup(){
        RestAssured.port = port;

        userService.dbStore(userId, userName);
        WorkspaceResponse workspace = workspaceService.createWorkspace(getWorkspaceRequest(), getCurrentUser());
        workspaceId = workspace.getId();
    }


    @DisplayName("유저가 속한 워크스페이스들을 조회할 수 있다.")
    @Test
    public void getWorkspaces(){

        ExtractableResponse<Response> response = 워크스페이스_리스트_조회();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    }

    @DisplayName("워크스페이스 아이디를 가지고 워크스페이스를 조회할 수 있다.")
    @Test
    public void getWorkspace(){
        ExtractableResponse<Response> response = 워크스페이스_조회(workspaceId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("워크스페이스를 생성할 수 있다.")
    @Test
    public void createWorkspace(){
        ExtractableResponse<Response> response = 워크스페이스_생성(getWorkspaceRequest());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("워크스페이스를 업데이트 할 수 있다.")
    @Test
    public void updateWorkspace(){
        ExtractableResponse<Response> response = 워크스페이스_수정(getWorkspaceRequest(),workspaceId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("워크스페이스를 삭제 할 수 있다.")
    @Test
    public void deleteWorkspace(){
        ExtractableResponse<Response> response = 워크스페이스_삭제(workspaceId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }




    private ExtractableResponse<Response> 워크스페이스_조회(long workspaceId){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/workspaces/{workspaceId}", workspaceId)
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 워크스페이스_리스트_조회(){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/workspaces")
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 워크스페이스_생성(WorkspaceRequest workspaceRequest){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .body(workspaceRequest)
                .when()
                .post("/workspaces")
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 워크스페이스_수정(WorkspaceRequest workspaceRequest, Long workspaceId){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .body(workspaceRequest)
                .when()
                .put("/workspaces/{workspaceId}", workspaceId)
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 워크스페이스_삭제(Long workspaceId){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .when()
                .delete("/workspaces/{workspaceId}", workspaceId)
                .then()
                .log()
                .all()
                .extract();
    }
    private WorkspaceRequest getWorkspaceRequest(){
        return new WorkspaceRequest(workspaceName, workspaceEmail);
    }


    private CurrentUser getCurrentUser(){
        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserId(userId);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(currentUser, null, List.of(new SimpleGrantedAuthority(("USER"))));

        return currentUser;
    }

}

