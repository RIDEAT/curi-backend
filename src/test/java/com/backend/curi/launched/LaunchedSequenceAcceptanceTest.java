//package com.backend.curi.launched;
//
//
//import com.backend.curi.common.Constants;
//import com.backend.curi.launched.controller.dto.LaunchedSequenceRequest;
//import com.backend.curi.launched.controller.dto.LaunchedSequenceResponse;
//import com.backend.curi.launched.repository.entity.LaunchedSequence;
//import com.backend.curi.launched.controller.dto.LaunchedWorkflowRequest;
//import com.backend.curi.launched.controller.dto.LaunchedWorkflowResponse;
//import com.backend.curi.launched.repository.entity.LaunchedStatus;
//import com.backend.curi.member.controller.dto.EmployeeRequest;
//import com.backend.curi.member.controller.dto.ManagerRequest;
//import com.backend.curi.member.repository.entity.MemberType;
//import com.backend.curi.member.service.MemberService;
//import com.backend.curi.security.dto.CurrentUser;
//import com.backend.curi.user.service.UserService;
//import com.backend.curi.workflow.controller.dto.WorkflowRequest;
//import com.backend.curi.workflow.controller.dto.WorkflowResponse;
//import com.backend.curi.workflow.service.WorkflowService;
//import com.backend.curi.workspace.controller.dto.WorkspaceRequest;
//import com.backend.curi.workspace.controller.dto.WorkspaceResponse;
//import com.backend.curi.workspace.service.WorkspaceService;
//import io.restassured.http.ContentType;
//import io.restassured.response.ExtractableResponse;
//import io.restassured.response.Response;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.BeforeEach;
//import io.restassured.RestAssured;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.test.context.TestPropertySource;
//
//import javax.transaction.Transactional;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//@TestPropertySource(locations = "classpath:application-data.properties")
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Transactional
//public class LaunchedSequenceAcceptanceTest {
//
//    @LocalServerPort
//    public int port;
//
//    private final String userId = Constants.userId;
//    private final String userEmail = Constants.userEmail;
//    private final String workspaceName = Constants.workspaceName;
//    private final String workspaceEmail = Constants.workspaceEmail;
//    private final String workflowName = Constants.workflowName;
//    private final String authToken = Constants.authToken;
//    private Long workspaceId;
//    private Long employeeId;
//    private Long managerId;
//
//    private Long workflowId;
//
//    private Long launchedworkflowId;
//    private Long launchedsequenceId;
//
//    @BeforeEach
//    public void setup() {
//        RestAssured.port = port;
//        workspaceId = 295L;
//        workflowId = 205L;
//        launchedworkflowId = 7L;
//        launchedsequenceId = 16L;
//    }
//
//    @DisplayName("런치드 워크플로우에 속한 시퀀스를 조회할 수 있다.")
//    @Test
//    public void getLaunchedWorkflowList(){
//        ExtractableResponse<Response> response = 런치드_워크플로우내_시퀀스_조회();
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
//    }
//
//
//
//
//    @DisplayName("런치드 워크플로우내의 시퀀스 정보를 수정할 수 있다.")
//    @Test
//    public void updateLaunchedWorkflow(){
//        ExtractableResponse<Response> getResponseBeforeUpdate = 런치드_워크플로우내_시퀀스_조회();
//        LaunchedSequenceResponse originalSequenceResponse = getResponseBeforeUpdate.as(LaunchedSequenceResponse.class);
////        ExtractableResponse<Response> updateResponse = 런치드_워크플로우내_시퀀스_수정(getModifiedLaunchedSequenceRequest(originalSequenceResponse));
////        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
//
///*
//        ExtractableResponse<Response> getResponseAfterUpdate = 런치드_워크플로우내_시퀀스_조회();
//        LaunchedSequenceResponse updatedSequenceResponse = getResponseAfterUpdate.as(LaunchedSequenceResponse.class);
//        assertThat(updatedSequenceResponse.getStatus()).isEqualTo(getModifiedLaunchedSequenceRequest(originalSequenceResponse).getStatus());
//  */
//    }
//
//
//    /*
//    @DisplayName("워크 스페이스 내에 런치드 워크 플로우를 삭제할 수 있다.")
//    @Test
//    public void deleteLaunchedWorkflow(){
//        ExtractableResponse<Response> response = 런치드_워크플로우_삭제();
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
//
//        ExtractableResponse<Response> getResponse = 워크스페이스내_런치드_워크플로우_조회();
//        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
//    }*/
//
//    private ExtractableResponse<Response> 런치드_워크플로우내_시퀀스_조회(){
//        return RestAssured.
//                given()
//                .header("Authorization", "Bearer " + authToken)
//                .when()
//                .get("/workspaces/{workspaceId}/launchedworkflows/{launchedworkflowId}/sequences/{sequenceId}",workspaceId, launchedworkflowId, launchedsequenceId)
//                .then()
//                .log()
//                .all()
//                .extract();
//    }
//
//
//
//
//
//
//    private ExtractableResponse<Response> 런치드_워크플로우내_시퀀스_수정(LaunchedSequenceRequest launchedSequenceRequest){
//        return RestAssured.
//                given()
//                .header("Authorization", "Bearer " + authToken)
//                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
//                .body(launchedSequenceRequest)
//                .when()
//                .put("/workspaces/{workspaceId}/launchedworkflows/{launchedworkflowId}/sequences/{sequenceId}", workspaceId, launchedworkflowId, launchedsequenceId)
//                .then()
//                .log()
//                .all()
//                .extract();
//    }
//
//    private ExtractableResponse<Response> 런치드_워크플로우_삭제(){
//        return RestAssured.
//                given()
//                .header("Authorization", "Bearer " + authToken)
//                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
//                .when()
//                .delete("/workspaces/{workspaceId}/launchedworkflows/{launchedworkflowId}", workspaceId, launchedworkflowId)
//                .then()
//                .log()
//                .all()
//                .extract();
//    }
//    private WorkspaceRequest getWorkspaceRequest(){
//        return new WorkspaceRequest(workspaceName, workspaceEmail);
//    }
//
//    private EmployeeRequest getEmployeeRequest(){
//        EmployeeRequest employeeRequest = new EmployeeRequest();
//        employeeRequest.setName("terry cho");
//        employeeRequest.setEmail("terry@gmail.com");
//        employeeRequest.setStartDate("2020-10-09");
//        employeeRequest.setWid(workspaceId);
//        employeeRequest.setDepartment("back-end");
//        employeeRequest.setPhoneNum("010-2431-2298");
//        employeeRequest.setManagers(new ArrayList<>());
//        return employeeRequest;
//    }
//
//    private EmployeeRequest getModifiedEmployeeRequest(){
//        EmployeeRequest employeeRequest = new EmployeeRequest();
//        employeeRequest.setName("terry cho");
//        employeeRequest.setEmail("terry@gmail.com");
//        employeeRequest.setStartDate("2020-10-09");
//        employeeRequest.setWid(workspaceId);
//        employeeRequest.setDepartment("front-end");
//        employeeRequest.setPhoneNum("010-2431-2298");
//        employeeRequest.setManagers(new ArrayList<>());
//        return employeeRequest;
//    }
//
//    private ManagerRequest getManagerRequest(){
//        ManagerRequest managerRequest = new ManagerRequest();
//        managerRequest.setWid(workspaceId);
//        managerRequest.setDepartment("back-end");
//        managerRequest.setName("juram");
//        managerRequest.setEmail("juram@gmail.com");
//        managerRequest.setPhoneNum("010-3333-2222");
//        return managerRequest;
//    }
//    private WorkflowRequest getWorkflowRequest(){
//        WorkflowRequest workflowRequest = new WorkflowRequest();
//        workflowRequest.setName(workflowName);
//        return workflowRequest;
//    }
//
//    private LaunchedSequenceRequest getModifiedLaunchedSequenceRequest(LaunchedSequenceResponse originalLaunchedSequenceResponse){
//        LaunchedSequenceRequest launchedSequenceRequest = new LaunchedSequenceRequest();
//        launchedSequenceRequest.setWorkflowId(launchedworkflowId);
//        launchedSequenceRequest.setStatus(LaunchedStatus.IN_PROGRESS);
//        launchedSequenceRequest.setApplyDate(originalLaunchedSequenceResponse.getApplyDate());
//        launchedSequenceRequest.setName(originalLaunchedSequenceResponse.getName());
//        launchedSequenceRequest.setEmployeeId(originalLaunchedSequenceResponse.getAssignedMember().getId());
//        return launchedSequenceRequest;
//
//    }
//    private LaunchedWorkflowRequest getModifiedLaunchedWorkflowRequest(LaunchedWorkflowResponse originalLaunchedWorkflowResponse){
//        LaunchedWorkflowRequest workflowRequest = new LaunchedWorkflowRequest();
//        workflowRequest.setWorkflowId(workflowId);
//        workflowRequest.setStatus(LaunchedStatus.IN_PROGRESS);
//        workflowRequest.setKeyDate(originalLaunchedWorkflowResponse.getKeyDate());
//        workflowRequest.setName(originalLaunchedWorkflowResponse.getName());
//        return workflowRequest;
//    }
//
//    private CurrentUser getCurrentUser(){
//        CurrentUser currentUser = new CurrentUser();
//        currentUser.setUserId(userId);
//        currentUser.setUserEmail(userEmail);
//        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(currentUser, null, List.of(new SimpleGrantedAuthority(("USER"))));
//
//        return currentUser;
//    }
//
//}
//
//
//
