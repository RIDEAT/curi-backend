package com.backend.curi.workflow;


import com.backend.curi.common.Constants;
import com.backend.curi.member.controller.dto.MemberRequest;
import com.backend.curi.member.repository.entity.MemberType;
import com.backend.curi.member.service.MemberService;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.service.UserService;
import com.backend.curi.workflow.controller.dto.SequenceRequest;
import com.backend.curi.workflow.controller.dto.SequenceResponse;
import com.backend.curi.workflow.controller.dto.WorkflowRequest;
import com.backend.curi.workflow.controller.dto.WorkflowResponse;
import com.backend.curi.workflow.service.SequenceService;
import com.backend.curi.workflow.service.WorkflowService;
import com.backend.curi.workspace.controller.dto.WorkspaceRequest;
import com.backend.curi.workspace.controller.dto.WorkspaceResponse;
import com.backend.curi.workspace.service.RoleService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-data.properties")
public class SequenceAcceptanceTest {


    @Autowired
    private UserService userService;
    @Autowired
    private WorkspaceService workspaceService;
    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private SequenceService sequenceService;

    @Autowired
    private RoleService roleService;
    @Autowired
    private MemberService memberService;

    @LocalServerPort
    public int port;

    private final String userId = Constants.userEmail;
    private final String userEmail = Constants.userEmail;
    private final String userName = Constants.userName;

    private final String workspaceName = Constants.workspaceName;
    private final String workspaceEmail = Constants.workspaceEmail;
    private final String workflowName = Constants.workflowName;
    private final String authToken = Constants.authToken;
    private Long workspaceId;
    private Long employeeId;
    private Long managerId;

    private Long workflowId;

    private Long sequenceId;

    private Long defaultRoleId;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;

        userService.dbStore(userId, userName);
        WorkspaceResponse workspaceResponse = workspaceService.createWorkspace(getWorkspaceRequest(), getCurrentUser());
        workspaceId = workspaceResponse.getId();
        defaultRoleId = workspaceResponse.getRoles().get(1).getId();

        var managerResponse = memberService.createMember(getManagerRequest());
        var employeeResponse = memberService.createMember(getEmployeeRequest());


        managerId = managerResponse.getId();
        employeeId = employeeResponse.getId();

        var workflowResponse = workflowService.createWorkflow(workspaceId, getWorkflowRequest());
        workflowId = workflowResponse.getId();

        var sequenceInWorkflow = sequenceService.createSequence(workspaceId, workflowId,getSequenceRequest());
        sequenceId = sequenceInWorkflow.getId();

    }


    @DisplayName("워크스페이스에 속한 시퀀스를 조회할 수 있다.")
    @Test
    public void getSequence(){
        ExtractableResponse<Response> response = 워크스페이스내_시퀀스_조회();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    }



    @DisplayName("워크스페이스 내에 워크플로우 아래에 시퀀스를 추가할 수 있다.")
    @Test
    public void createSequenceInWorkflow(){
        ExtractableResponse<Response> sequenceResponse = 워크플로우내_시퀀스_생성(getSequenceRequest());
        assertThat(sequenceResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        ExtractableResponse<Response> workflowGetResponse = 워크스페이스내_워크플로우_조회();
        WorkflowResponse workflowResponse = workflowGetResponse.as(WorkflowResponse.class);

        assertThat(workflowResponse.getSequences().contains(sequenceResponse.as(SequenceResponse.class)));
    }



    @DisplayName("워크스페이스 내에 워크플로우 아래에 시퀀스를 수정할 수 있다.")
    @Test
    public void updateSequenceInWorkflow(){
        ExtractableResponse<Response> updatedResponse = 워크플로우내_시퀀스_수정(getModifiedSequenceRequest());
        assertThat(updatedResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> workflowGetResponse = 워크스페이스내_워크플로우_조회();
        WorkflowResponse workflowResponse = workflowGetResponse.as(WorkflowResponse.class);

        assertThat(workflowResponse.getSequences().contains(updatedResponse.as(SequenceResponse.class)));
    }


    @DisplayName("워크 스페이스 내에 워크플로우 아래에 템플릿 시퀀스를 삭제할 수 있다.")
    @Test
    public void deleteSequencInWorkflow(){
        ExtractableResponse<Response> workflowGetResponseBeforeDeleting = 워크스페이스내_워크플로우_조회();
        WorkflowResponse workflowResponseBeforeDeleting = workflowGetResponseBeforeDeleting.as(WorkflowResponse.class);

        assertThat(!workflowResponseBeforeDeleting.getSequences().isEmpty());

        ExtractableResponse<Response> response = 워크플로우내_시퀀스_삭제();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> workflowGetResponse = 워크스페이스내_워크플로우_조회();
        WorkflowResponse workflowResponse = workflowGetResponse.as(WorkflowResponse.class);

        assertThat(workflowResponse.getSequences().isEmpty());
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

    private ExtractableResponse<Response> 워크스페이스내_시퀀스_조회(){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/workspaces/{workspaceId}/workflows/{workflows}/sequences/{sequenceId}",workspaceId, workflowId, sequenceId)
                .then()
                .log()
                .all()
                .extract();
    }



    private ExtractableResponse<Response> 워크플로우내_시퀀스_생성(SequenceRequest sequenceRequest){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .body(sequenceRequest)
                .when()
                .post("/workspaces/{workspaceId}/workflows/{workflowId}/sequences", workspaceId, workflowId)
                .then()
                .log()
                .all()
                .extract();
    }


    private ExtractableResponse<Response> 워크플로우내_시퀀스_수정(SequenceRequest sequenceRequest){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .body(sequenceRequest)
                .when()
                .put("/workspaces/{workspaceId}/workflows/{workflowId}/sequences/{sequenceId}", workspaceId, workflowId, sequenceId)
                .then()
                .log()
                .all()
                .extract();
    }


    private ExtractableResponse<Response> 워크플로우내_시퀀스_삭제(){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .when()
                .delete("/workspaces/{workspaceId}/workflows/{workflowId}/sequences/{sequenceId}", workspaceId, workflowId, sequenceId)
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


    private SequenceRequest getSequenceRequest() {
        SequenceRequest sequenceRequest = new SequenceRequest();
        sequenceRequest.setName("신입 환영 시퀀스");
        sequenceRequest.setDayOffset(-2);
       // sequenceRequest.setPrevSequenceId(0L);
        sequenceRequest.setRoleId(defaultRoleId);

        return sequenceRequest;
    }

    private SequenceRequest getModifiedSequenceRequest() {
        SequenceRequest sequenceRequest = new SequenceRequest();
        sequenceRequest.setName("담당 사수와의 미팅");
        sequenceRequest.setDayOffset(-2);
        //sequenceRequest.setPrevSequenceId(0L);
        sequenceRequest.setRoleId(defaultRoleId);

        return sequenceRequest;
    }



    private CurrentUser getCurrentUser(){
        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserId(userId);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(currentUser, null, List.of(new SimpleGrantedAuthority(("USER"))));

        return currentUser;
    }

}



