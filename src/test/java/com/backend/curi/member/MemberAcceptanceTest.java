package com.backend.curi.member;

import com.backend.curi.common.Constants;
import com.backend.curi.member.controller.dto.MemberRequest;
import com.backend.curi.member.controller.dto.MemberResponse;
import com.backend.curi.member.repository.entity.MemberType;
import com.backend.curi.member.service.MemberService;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-data.properties")
public class MemberAcceptanceTest {


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
    private final String userEmail = Constants.userEmail;
    private final String userName = Constants.userName;

    private final String workspaceName = Constants.workspaceName;
    private final String workspaceEmail = Constants.workspaceEmail;
    private final String workflowName = Constants.workflowName;
    private final String authToken = Constants.authToken;
    private Long workspaceId;
    private Long employeeId;
    private Long managerId;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;

        userService.dbStore(userId, userName);
        WorkspaceResponse workspace = workspaceService.createWorkspace(getWorkspaceRequest(), getCurrentUser());
        workspaceId = workspace.getId();

        var managerResponse = memberService.createMember(getManagerRequest());
        var employeeResponse = memberService.createMember(getEmployeeRequest());

        managerId = managerResponse.getId();
        employeeId = employeeResponse.getId();

    }


    @DisplayName("워크스페이스에 속한 신입사원들을 조회할 수 있다.")
    @Test
    public void getEmployees(){
        ExtractableResponse<Response> response = 워크스페이스내_신입사원_리스트_조회();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());


    }
    @DisplayName("워크스페이스에 속한 기존직원들을 조회할 수 있다.")
    @Test
    public void getManagers(){
        ExtractableResponse<Response> response = 워크스페이스내_기존직원_리스트_조회();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());


    }

    @DisplayName("워크스페이스 내에 신입사원을 추가할 수 있다.")
    @Test
    public void createEmployee(){
        ExtractableResponse<Response> response = 신입사원_생성(getEmployeeRequest());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("워크스페이스 내에 기존 직원을 추가할 수 있다.")
    @Test
    public void createManager(){
        ExtractableResponse<Response> response = 기존직원_생성(getManagerRequest());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    }

    @DisplayName("신입사원 정보를 수정할 수 있다.")
    @Test
    public void updateEmployee(){
        ExtractableResponse<Response> response = 신입사원_수정(getModifiedEmployeeRequest());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        MemberResponse updatedEmployeeResponse = response.as(MemberResponse.class);
        assertThat(updatedEmployeeResponse.getDepartment()).isEqualTo(getModifiedEmployeeRequest().getDepartment());
    }

    @DisplayName("기존 직원 정보를 수정할 수 있다.")
    @Test
    public void updateManager(){
        ExtractableResponse<Response> response = 기존직원_수정(getModifiedManagerRequest());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        MemberResponse updatedManagerResponse = response.as(MemberResponse.class);
        assertThat(updatedManagerResponse.getDepartment()).isEqualTo(getModifiedManagerRequest().getDepartment());
    }

    @DisplayName("워크 스페이스 내에 신입사원을 삭제할 수 있다.")
    @Test
    public void deleteEmployee(){
        ExtractableResponse<Response> response = 신입사원_삭제();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("워크 스페이스 내에 직원을 삭제할 수 있다.")
    @Test
    public void deleteManger(){
        ExtractableResponse<Response> response = 기존직원_삭제();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }






    private ExtractableResponse<Response> 워크스페이스내_신입사원_리스트_조회(){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/workspaces/{workspaceId}/members?type=employee",workspaceId)
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 워크스페이스내_기존직원_리스트_조회(){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/workspaces/{workspaceId}/members?type=manager",workspaceId)
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 신입사원_생성(MemberRequest employeeRequest){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .body(employeeRequest)
                .when()
                .post("/workspaces/{workspaceId}/members", workspaceId)
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 기존직원_생성(MemberRequest managerRequest){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .body(managerRequest)
                .when()
                .post("/workspaces/{workspaceId}/members", workspaceId)
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 신입사원_수정(MemberRequest employeeRequest){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .body(employeeRequest)
                .when()
                .put("/workspaces/{workspaceId}/members/{mid}", workspaceId, employeeId )
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 기존직원_수정(MemberRequest managerRequest){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .body(managerRequest)
                .when()
                .put("/workspaces/{workspaceId}/members/{mid}", workspaceId, managerId)
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 신입사원_삭제(){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .when()
                .delete("/workspaces/{workspaceId}/members/{mid}", workspaceId, employeeId)
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 기존직원_삭제(){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .when()
                .delete("/workspaces/{workspaceId}/members/{mid}", workspaceId, managerId)
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
        employeeRequest.setEmail("terry63@gmail.com");
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
        employeeRequest.setEmail("terry63@gmail.com");
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
        managerRequest.setStartDate("2020-10-09");
        managerRequest.setName("juram");
        managerRequest.setEmail("juram63@gmail.com");
        managerRequest.setPhoneNum("010-3333-2222");
        managerRequest.setType(MemberType.manager);
        return managerRequest;
    }

    private MemberRequest getModifiedManagerRequest(){
        MemberRequest managerRequest = new MemberRequest();
        managerRequest.setWid(workspaceId);
        managerRequest.setDepartment("front-end");
        managerRequest.setName("juram");
        managerRequest.setEmail("juram63@unist.ac.kr");
        managerRequest.setPhoneNum("010-3333-2222");
        managerRequest.setType(MemberType.manager);
        managerRequest.setStartDate("2020-10-09");
        return managerRequest;
    }

    private CurrentUser getCurrentUser(){
        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserId(userId);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(currentUser, null, List.of(new SimpleGrantedAuthority(("USER"))));

        return currentUser;
    }

}


