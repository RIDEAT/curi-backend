package com.backend.curi.launched;


import com.backend.curi.common.Constants;
import com.backend.curi.common.feign.SchedulerOpenFeign;
import com.backend.curi.common.feign.dto.SequenceMessageRequest;
import com.backend.curi.launched.controller.dto.LaunchedWorkflowRequest;
import com.backend.curi.launched.controller.dto.LaunchedWorkflowResponse;
import com.backend.curi.launched.repository.entity.LaunchedStatus;
import com.backend.curi.member.controller.dto.EmployeeManagerDetail;
import com.backend.curi.member.controller.dto.MemberRequest;
import com.backend.curi.member.repository.entity.MemberType;
import com.backend.curi.member.service.MemberService;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.slack.controller.dto.OAuthRequest;
import com.backend.curi.slack.controller.dto.SlackMessageRequest;
import com.backend.curi.slack.service.SlackService;
import com.backend.curi.user.service.UserService;
import com.backend.curi.workflow.controller.dto.*;
import com.backend.curi.workflow.repository.entity.ModuleType;
import com.backend.curi.workflow.service.LaunchService;
import com.backend.curi.workflow.service.ModuleService;
import com.backend.curi.workflow.service.SequenceService;
import com.backend.curi.workflow.service.WorkflowService;
import com.backend.curi.workspace.controller.dto.WorkspaceRequest;
import com.backend.curi.workspace.controller.dto.WorkspaceResponse;
import com.backend.curi.workspace.service.RoleService;
import com.backend.curi.workspace.service.WorkspaceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import io.restassured.RestAssured;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.context.TestPropertySource;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-data.properties")
public class LaunchedWorkflowAcceptanceTest {

    @MockBean
    private SchedulerOpenFeign schedulerOpenFeign;

    @MockBean
    private SlackService slackService;
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

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private LaunchService launchService;

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
    private Long workspaceId2;
    private Long employeeId;
    private Long directManagerId;

    private Long hrManagerId;
    private Long workflowId;

    private Long sequenceId;

    private Long sequenceInWorkflowId;
    private Long sequenceInWorkflowId2;
    private Long employeeRoleId;


    private Long directManagerRoleId;
    private Long hrManagerRoleId;

    private Long launchedworkflowId;

    private Long templateModuleId;
    private Long moduleInSequenceId;



    @BeforeEach
    public void setup() throws JsonProcessingException {
        defaultSet();
        userMakeWorkspace();
        userMakeEmployeeAndManager();
        userMakeWorkspaceSequenceModule();
        userLaunchWorkflow();
    }

    private void defaultSet (){
        when(slackService.sendMessage(any(SlackMessageRequest.class)))
                .thenReturn(new ChatPostMessageResponse());

        when(schedulerOpenFeign.createMessage(any(SequenceMessageRequest.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

        when(schedulerOpenFeign.deleteMessage(any(Long.class)))
                .thenReturn(ResponseEntity.noContent().build());

        RestAssured.port = port;

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(getCurrentUser());
    }


    private void userMakeWorkspace(){
        userService.dbStore(userId, userName);
        WorkspaceResponse workspaceResponse = workspaceService.createWorkspace(getWorkspaceRequest(), getCurrentUser());
        WorkspaceResponse workspaceResponse2 = workspaceService.createWorkspace(getWorkspaceRequest(), getCurrentUser());
        workspaceId = workspaceResponse.getId();
        workspaceId2= workspaceResponse2.getId();
        employeeRoleId = workspaceResponse.getRoles().get(0).getId();
        directManagerRoleId = workspaceResponse.getRoles().get(1).getId();
        hrManagerRoleId = workspaceResponse.getRoles().get(2).getId();    }

    private void userMakeEmployeeAndManager(){
        var managerResponse = memberService.createMember(getDirectManagerRequest());

        directManagerId= managerResponse.getId();

        var hrManagerResponse = memberService.createMember(getHrManagerRequest());

        hrManagerId = hrManagerResponse.getId();

        var employeeResponse = memberService.createMember(getEmployeeRequest());

        employeeId = employeeResponse.getId();
    }

    private void userMakeWorkspaceSequenceModule(){
        var workflowResponse = workflowService.createWorkflow(workspaceId, getWorkflowRequest());

        workflowId = workflowResponse.getId();

        var sequenceInWorkflow = sequenceService.createSequence(workspaceId, workflowId,getSequenceRequest());
        sequenceInWorkflowId = sequenceInWorkflow.getId();

        var sequenceInWorkflow2 = sequenceService.createSequence(workspaceId, workflowId,getSequenceRequest2());
        sequenceInWorkflowId2 = sequenceInWorkflow2.getId();

        var moduleInSequence = moduleService.createModule(workspaceId, sequenceInWorkflowId, getModuleRequest());
        moduleInSequenceId = moduleInSequence.getId();
    }

    private void userLaunchWorkflow() throws JsonProcessingException {
        var launchedWorkflow = launchService.launchWorkflows(workflowId, getLaunchRequest(), workspaceId);
        launchedworkflowId = launchedWorkflow.getEmployees().get(0).getLaunchedWorkflowId();
    }


    @DisplayName("워크스페이스에 속한 런치드 워크플로우 리스트를 조회할 수 있다.")
    @Test
    public void getLaunchedWorkflowList(){
        ExtractableResponse<Response> response = 워크스페이스내_런치드_워크플로우_리스트_조회();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("워크스페이스에 속한 런치드 워크플로우를 조회할 수 있다.")
    @Test
    public void getLaunchedWorkflow(){
        ExtractableResponse<Response> response = 워크스페이스내_런치드_워크플로우_조회();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("다른 워크스페이스에 속한 런치드 워크플로우를 조회할 수 없다.")
    @Test
    public void getLaunchedWorkflowInOtherWorkspace(){
        ExtractableResponse<Response> response = 다른_워크스페이스내_런치드_워크플로우_조회();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("런치드 워크플로우 정보를 수정할 수 있다.")
    @Test
    public void updateLaunchedWorkflow(){
        ExtractableResponse<Response> getResponseBeforeUpdate = 워크스페이스내_런치드_워크플로우_조회();
        LaunchedWorkflowResponse originalWorkflowResponse = getResponseBeforeUpdate.as(LaunchedWorkflowResponse.class);
        ExtractableResponse<Response> updateResponse = 런치드_워크플로우_수정(getModifiedLaunchedWorkflowRequest(originalWorkflowResponse));
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());


        ExtractableResponse<Response> getResponseAfterUpdate = 워크스페이스내_런치드_워크플로우_조회();
        LaunchedWorkflowResponse updatedWorkflowResponse = getResponseAfterUpdate.as(LaunchedWorkflowResponse.class);
        assertThat(updatedWorkflowResponse.getStatus()).isEqualTo(getModifiedLaunchedWorkflowRequest(originalWorkflowResponse).getStatus());
    }

    /*
    @DisplayName("워크 스페이스 내에 런치드 워크 플로우를 삭제할 수 있다.")
    @Test
    public void deleteLaunchedWorkflow(){
        ExtractableResponse<Response> response = 런치드_워크플로우_삭제();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> getResponse = 워크스페이스내_런치드_워크플로우_조회();
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }*/

    private ExtractableResponse<Response> 워크스페이스내_런치드_워크플로우_리스트_조회(){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/workspaces/{workspaceId}/launchedworkflows",workspaceId)
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 다른_워크스페이스내_런치드_워크플로우_조회(){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/workspaces/{workspaceId}/launchedworkflows/{launchedworkflowId}",workspaceId2, launchedworkflowId)
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 워크스페이스내_런치드_워크플로우_조회(){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/workspaces/{workspaceId}/launchedworkflows/{launchedworkflowId}",workspaceId, launchedworkflowId)
                .then()
                .log()
                .all()
                .extract();
    }


    private ExtractableResponse<Response> 런치드_워크플로우_수정(LaunchedWorkflowRequest launchedWorkflowRequest){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .body(launchedWorkflowRequest)
                .when()
                .put("/workspaces/{workspaceId}/launchedworkflows/{launchedworkflowId}", workspaceId, launchedworkflowId)
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 런치드_워크플로우_삭제(){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .when()
                .delete("/workspaces/{workspaceId}/launchedworkflows/{launchedworkflowId}", workspaceId, launchedworkflowId)
                .then()
                .log()
                .all()
                .extract();
    }



    private WorkspaceRequest getWorkspaceRequest(){
        return new WorkspaceRequest(workspaceName, workspaceEmail);
    }

    private LaunchedWorkflowRequest getModifiedLaunchedWorkflowRequest(LaunchedWorkflowResponse originalLaunchedWorkflowResponse){
        LaunchedWorkflowRequest workflowRequest = new LaunchedWorkflowRequest();
        workflowRequest.setWorkflowId(workflowId);
        workflowRequest.setStatus(LaunchedStatus.IN_PROGRESS);
        workflowRequest.setKeyDate(originalLaunchedWorkflowResponse.getKeyDate());
        workflowRequest.setName(originalLaunchedWorkflowResponse.getName());
        return workflowRequest;
    }

    private MemberRequest getEmployeeRequest(){
        MemberRequest employeeRequest = new MemberRequest();
        employeeRequest.setName("terry cho");
        employeeRequest.setEmail("8514199@gmail.com");
        employeeRequest.setStartDate("2020-10-09");
        employeeRequest.setWid(workspaceId);
        employeeRequest.setDepartment("back-end");
        employeeRequest.setPhoneNum("010-2431-2298");
        employeeRequest.setType(MemberType.employee);
        return employeeRequest;
    }

    private List<EmployeeManagerDetail> getManagers(){
        List<EmployeeManagerDetail> employeeManagerDetails = new ArrayList<>();
        EmployeeManagerDetail employeeManagerDetail = new EmployeeManagerDetail();
        employeeManagerDetail.setId(directManagerId);
        employeeManagerDetail.setName("juram");
        employeeManagerDetail.setRoleId(directManagerRoleId);
        employeeManagerDetail.setRoleName("담당사수");
        employeeManagerDetails.add(employeeManagerDetail);

        EmployeeManagerDetail employeeManagerDetail2 = new EmployeeManagerDetail();
        employeeManagerDetail2.setId(hrManagerId);
        employeeManagerDetail2.setName("hanna");
        employeeManagerDetail2.setRoleId(hrManagerRoleId);
        employeeManagerDetail2.setRoleName("hr매니저");
        employeeManagerDetails.add(employeeManagerDetail2);

        return employeeManagerDetails;
    }


    private MemberRequest getDirectManagerRequest(){
        MemberRequest managerRequest = new MemberRequest();
        managerRequest.setWid(workspaceId);
        managerRequest.setDepartment("back-end");
        managerRequest.setName("juram");
        managerRequest.setEmail("8514199@naver.com");
        managerRequest.setPhoneNum("010-3333-2222");
        managerRequest.setStartDate("2020-10-09");
        managerRequest.setType(MemberType.manager);
        return managerRequest;
    }


    private MemberRequest getHrManagerRequest(){
        MemberRequest managerRequest = new MemberRequest();
        managerRequest.setWid(workspaceId);
        managerRequest.setDepartment("HR");
        managerRequest.setName("hanna");
        managerRequest.setEmail("rideat63@gmail.com");
        managerRequest.setPhoneNum("010-1111-2222");
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
        sequenceRequest.setPrevSequenceId(0L);
        sequenceRequest.setRoleId(hrManagerRoleId);

        return sequenceRequest;
    }

    private SequenceRequest getSequenceRequest2() {
        SequenceRequest sequenceRequest = new SequenceRequest();
        sequenceRequest.setName("관계자 미팅 시퀀스");
        sequenceRequest.setDayOffset(0);
        sequenceRequest.setPrevSequenceId(0L);
        sequenceRequest.setRoleId(hrManagerRoleId);

        return sequenceRequest;
    }


    private SequenceRequest getSequenceRequest3() {
        SequenceRequest sequenceRequest = new SequenceRequest();
        sequenceRequest.setName("코드 리뷰 시퀀스");
        sequenceRequest.setDayOffset(1);
        sequenceRequest.setPrevSequenceId(0L);
        sequenceRequest.setRoleId(hrManagerRoleId);

        return sequenceRequest;
    }


    private SequenceRequest getModifiedSequenceRequest() {
        SequenceRequest sequenceRequest = new SequenceRequest();
        sequenceRequest.setName("담당 사수와의 미팅");
        sequenceRequest.setDayOffset(-2);
        sequenceRequest.setPrevSequenceId(0L);
        sequenceRequest.setRoleId(directManagerRoleId);

        return sequenceRequest;
    }

    private OAuthRequest getOauthRequest(){
        return new OAuthRequest("5305401263955.5804524543283.119c272b7c815671b9b9f3998910cfe6f9fd22a564fa44a6cf97a23b27b0edc2");
    }

    private List<LaunchRequest> getLaunchRequest(){
        LaunchRequest launchRequest = new LaunchRequest();
        launchRequest.setMemberId(employeeId);
        launchRequest.setKeyDate(LocalDate.of(2000,10,9));
//        MemberRoleRequest memberRoleRequest = new MemberRoleRequest();
//        memberRoleRequest.setMemberId(employeeId);
//        memberRoleRequest.setRoleId(employeeRoleId);


        MemberRoleRequest memberRoleRequest2 = new MemberRoleRequest();
        memberRoleRequest2.setMemberId(hrManagerId);
        memberRoleRequest2.setRoleId(hrManagerRoleId);

        MemberRoleRequest memberRoleRequest3 = new MemberRoleRequest();
        memberRoleRequest3.setMemberId(directManagerId);
        memberRoleRequest3.setRoleId(directManagerRoleId);

        List<MemberRoleRequest> list = new ArrayList<>();
//        list.add(memberRoleRequest);
        list.add(memberRoleRequest2);
        list.add(memberRoleRequest3);

        launchRequest.setMembers(list);
        List<LaunchRequest> launchRequests = new ArrayList<>();
        launchRequests.add(launchRequest);
        return launchRequests;
    }

    private ModuleRequest getModuleRequest(){
        ModuleRequest moduleRequest = new ModuleRequest();
        moduleRequest.setName("hello new employee!");
        moduleRequest.setType(ModuleType.contents);
        moduleRequest.setOrder(1);
        return moduleRequest;
    }

    private ModuleRequest getModuleRequest2(){
        ModuleRequest moduleRequest = new ModuleRequest();
        moduleRequest.setName("담당 사수와 식사");
        moduleRequest.setType(ModuleType.contents);
        moduleRequest.setOrder(1);
        return moduleRequest;
    }

    private ModuleRequest getModuleRequest3(){
        ModuleRequest moduleRequest = new ModuleRequest();
        moduleRequest.setName("코드 리뷰");
        moduleRequest.setType(ModuleType.contents);
        moduleRequest.setOrder(1);
        return moduleRequest;
    }

    private ModuleRequest getModifiedModuleRequest(){
        ModuleRequest moduleRequest = new ModuleRequest();
        moduleRequest.setName("bye old employee!");
        moduleRequest.setType(ModuleType.contents);
        moduleRequest.setOrder(1);
        return moduleRequest;
    }
    private CurrentUser getCurrentUser(){
        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserId(userId);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(currentUser, null, List.of(new SimpleGrantedAuthority(("USER"))));

        return currentUser;
    }
}



