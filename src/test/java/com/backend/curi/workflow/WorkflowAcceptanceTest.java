package com.backend.curi.workflow;


import com.backend.curi.common.Constants;
import com.backend.curi.member.controller.dto.MemberRequest;
import com.backend.curi.member.repository.entity.MemberType;
import com.backend.curi.member.service.MemberService;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.service.UserService;
import com.backend.curi.workflow.controller.dto.WorkflowRequest;
import com.backend.curi.workflow.controller.dto.WorkflowResponse;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-data.properties")
public class WorkflowAcceptanceTest {


    @Autowired
    private UserService userService;
    @Autowired
    private WorkspaceService workspaceService;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private MemberService memberService;

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
    private Long workspaceId2;

    private Long employeeId;
    private Long managerId;

    private Long workflowId;


    @BeforeEach
    public void setup() {
        RestAssured.port = port;

        userService.dbStore(userId, userName);
        WorkspaceResponse workspace = workspaceService.createWorkspace(getWorkspaceRequest(), getCurrentUser());
        workspaceId = workspace.getId();

        WorkspaceResponse workspace2 = workspaceService.createWorkspace(getWorkspaceRequest(), getCurrentUser());
        workspaceId2 = workspace2.getId();

        var managerResponse = memberService.createMember(getManagerRequest());
        var employeeResponse = memberService.createMember(getEmployeeRequest());

        managerId = managerResponse.getId();
        employeeId = employeeResponse.getId();

        var workflowResponse = workflowService.createWorkflow(workspaceId, getWorkflowRequest());

        workflowId = workflowResponse.getId();

    }

    @DisplayName("워크스페이스에 속한 워크플로우 리스트를 조회할 수 있다.")
    @Test
    public void getWorkflows(){
        ExtractableResponse<Response> response = 워크스페이스내_워크플로우_리스트_조회();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    }


    @DisplayName("워크스페이스에 속한 워크플로우를 조회할 수 있다.")
    @Test
    public void getWorkflow(){
        ExtractableResponse<Response> response = 워크스페이스내_워크플로우_조회();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    }

    @DisplayName("워크스페이스에 속하지 않은 워크플로우를 조회할 수 없다")
    @Test
    public void getWorflowInOthersWorkflow(){
        ExtractableResponse<Response> response = 워크스페이스내_없는_워크플로우_조회();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }


    @DisplayName("워크스페이스 내에 워크플로우를 추가할 수 있다.")
    @Test
    public void createWorkflow(){
        ExtractableResponse<Response> response = 워크플로우_생성(getWorkflowRequest());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }


    @DisplayName("워크플로우 정보를 수정할 수 있다.")
    @Test
    public void updateWorkflow(){
        ExtractableResponse<Response> updateResponse = 워크플로우_수정(getModifiedWorkflowRequest());
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());


        ExtractableResponse<Response> getResponse = 워크스페이스내_워크플로우_조회();
        WorkflowResponse updatedWorkflowResponse = getResponse.as(WorkflowResponse.class);
        assertThat(updatedWorkflowResponse.getName()).isEqualTo(getModifiedWorkflowRequest().getName());
    }


    @DisplayName("워크 스페이스 내에 워크 플로우를 삭제할 수 있다.")
    @Test
    public void deleteEmployee(){
        ExtractableResponse<Response> response = 워크플로우_삭제();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> getResponse = 워크스페이스내_워크플로우_조회();
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> 워크스페이스내_워크플로우_리스트_조회(){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/workspaces/{workspaceId}/workflows",workspaceId)
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 워크스페이스내_워크플로우_조회(){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/workspaces/{workspaceId}/workflows/{workflowId}",workspaceId, workflowId)
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 워크스페이스내_없는_워크플로우_조회(){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/workspaces/{workspaceId}/workflows/{workflowId}",workspaceId2, workflowId)
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 워크플로우_생성(WorkflowRequest workflowRequest){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .body(workflowRequest)
                .when()
                .post("/workspaces/{workspaceId}/workflows", workspaceId)
                .then()
                .log()
                .all()
                .extract();
    }



    private ExtractableResponse<Response> 워크플로우_수정(WorkflowRequest workflowRequest){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .body(workflowRequest)
                .when()
                .put("/workspaces/{workspaceId}/workflows/{workflowId}", workspaceId, workflowId)
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 워크플로우_삭제(){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .when()
                .delete("/workspaces/{workspaceId}/workflows/{workflowId}", workspaceId, workflowId)
                .then()
                .log()
                .all()
                .extract();
    }
    private WorkspaceRequest getWorkspaceRequest(){
        return new WorkspaceRequest(workspaceName, workspaceEmail);
    }

    private MemberRequest getEmployeeRequest(){
        MemberRequest employeeRequest = new MemberRequest();
        employeeRequest.setName("terry cho");
        employeeRequest.setEmail("terry@gmail.com");
        employeeRequest.setStartDate("2020-10-09");
        employeeRequest.setWid(workspaceId);
        employeeRequest.setDepartment("back-end");
        employeeRequest.setPhoneNum("010-2431-2298");
        employeeRequest.setType(MemberType.employee);
        return employeeRequest;
    }

    private MemberRequest getModifiedEmployeeRequest(){
        MemberRequest employeeRequest = new MemberRequest();
        employeeRequest.setName("terry cho");
        employeeRequest.setEmail("terry@gmail.com");
        employeeRequest.setStartDate("2020-10-09");
        employeeRequest.setWid(workspaceId);
        employeeRequest.setDepartment("front-end");
        employeeRequest.setPhoneNum("010-2431-2298");
        employeeRequest.setType(MemberType.employee);
        return employeeRequest;
    }

    private MemberRequest getManagerRequest(){
        MemberRequest managerRequest = new MemberRequest();
        managerRequest.setWid(workspaceId);
        managerRequest.setDepartment("back-end");
        managerRequest.setName("juram");
        managerRequest.setEmail("juram@gmail.com");
        managerRequest.setPhoneNum("010-3333-2222");
        managerRequest.setStartDate("2020-10-09");
        managerRequest.setType(MemberType.manager);
        return managerRequest;
    }
    private WorkflowRequest getWorkflowRequest(){
        WorkflowRequest workflowRequest = new WorkflowRequest();
        workflowRequest.setName(workflowName);
        return workflowRequest;
    }

    private WorkflowRequest getModifiedWorkflowRequest(){
        WorkflowRequest workflowRequest = new WorkflowRequest();
        workflowRequest.setName("modifiedName");
        return workflowRequest;
    }

    private CurrentUser getCurrentUser(){
        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserId(userId);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(currentUser, null, List.of(new SimpleGrantedAuthority(("USER"))));

        return currentUser;
    }

}



