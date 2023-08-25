package com.backend.curi.workflow;


import com.backend.curi.common.Constants;
import com.backend.curi.common.feign.SchedulerOpenFeign;
import com.backend.curi.common.feign.dto.SequenceMessageRequest;
import com.backend.curi.launched.controller.dto.LaunchedWorkflowResponse;
import com.backend.curi.member.controller.dto.EmployeeManagerDetail;
import com.backend.curi.member.controller.dto.EmployeeRequest;
import com.backend.curi.member.controller.dto.ManagerRequest;
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
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import io.restassured.RestAssured;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-data.properties")
public class LaunchAcceptanceTest {


    @MockBean
    private SchedulerOpenFeign schedulerOpenFeign;


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
    private SlackService slackService;

    @Autowired
    private LaunchService launchService;

    @LocalServerPort
    public int port;

    private final String userId = Constants.userId;
    private final String userEmail = Constants.userEmail;
    private final String workspaceName = Constants.workspaceName;
    private final String workspaceEmail = Constants.workspaceEmail;
    private final String workflowName = Constants.workflowName;
    private final String authToken = Constants.authToken;
    private Long workspaceId;
    private Long employeeId;
    private Long directManagerId;

    private Long hrManagerId;

    private Long workflowId;

    private Long sequenceId;

    private Long employeeRoleId;
    private Long directManagerRoleId;
    private Long hrManagerRoleId;

    private Long templateModuleId;
    private Long moduleInSequenceId;


    @BeforeEach
    public void setup() {
        when(schedulerOpenFeign.createMessage(any(SequenceMessageRequest.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

        when(schedulerOpenFeign.deleteMessage(any(Long.class)))
                .thenReturn(ResponseEntity.noContent().build());



        RestAssured.port = port;

        userService.dbStore(userId, userEmail);
        WorkspaceResponse workspaceResponse = workspaceService.createWorkspace(getWorkspaceRequest(), getCurrentUser());
        workspaceId = workspaceResponse.getId();
        employeeRoleId = workspaceResponse.getRoles().get(0).getId();
        directManagerRoleId = workspaceResponse.getRoles().get(1).getId();
        hrManagerRoleId = workspaceResponse.getRoles().get(2).getId();

        var managerResponse = memberService.createMember(MemberType.manager, getManagerRequest());

        directManagerId= managerResponse.getId();

        var hrManagerResponse = memberService.createMember(MemberType.manager, getHrManagerRequest());

        hrManagerId = hrManagerResponse.getId();

        var employeeResponse = memberService.createMember(MemberType.employee, getEmployeeRequest());

        employeeId = employeeResponse.getId();

        var workflowResponse = workflowService.createWorkflow(workspaceId, getWorkflowRequest());
        workflowId = workflowResponse.getId();

        var sequenceInWorkflow = sequenceService.createSequence(workspaceId, workflowId, getSequenceRequest());
        sequenceId = sequenceInWorkflow.getId();


        var moduleInSequence = moduleService.createModule(workspaceId, sequenceId, getModuleRequest());
        moduleInSequenceId = moduleInSequence.getId();

    }

    @DisplayName("워크플로우 launch 전 필요한 role 정보를 받을 수 있다.")
    @Test
    public void getRequiredRoles(){
        ExtractableResponse<Response> response = 워크플로우_런처전_필요한_롤();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }


    @DisplayName("특정 워크플로우를 launch 시킬 수 있다.")
    @Test
    public void launchWorkflow(){
        slackService.oauthMember( new OAuthRequest("5761031201206.5794310066130.504f91ee78fcc2d24870a44756a29bf54526d85d5b266a14c4521b4aa42f5712"),hrManagerId);

        ExtractableResponse<Response> response = 워크스페이스내_워크플로우_런치();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        LaunchedWorkflowResponse launchedWorkflowResponse = response.as(LaunchedWorkflowResponse.class);

        launchService.sendLaunchedSequenceNotification(launchedWorkflowResponse.getLaunchedSequences().get(0).getId());
    }
/*
    @DisplayName("워크스페이스에 속한 시퀀스 리스트를 조회할 수 있다.")
    @Test
    public void getSequences(){
        ExtractableResponse<Response> response = 워크스페이스내_시퀀스_리스트_조회();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    }

 */



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
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> workflowGetResponse = 워크스페이스내_워크플로우_조회();
        WorkflowResponse workflowResponse = workflowGetResponse.as(WorkflowResponse.class);

        assertThat(workflowResponse.getSequences().isEmpty());
    }

    private ExtractableResponse<Response>워크플로우_런처전_필요한_롤(){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .body(getLaunchRequest())
                .when()
                .get("/workspaces/{workspaceId}/workflows/{workflowId}/requiredforlaunch",workspaceId, workflowId)
                .then()
                .log()
                .all()
                .extract();
    }
    private ExtractableResponse<Response> 워크스페이스내_워크플로우_런치(){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .body(getLaunchRequest())
                .when()
                .post("/workspaces/{workspaceId}/workflows/{workflowId}/launch",workspaceId, workflowId)
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

    private ExtractableResponse<Response> 워크스페이스내_시퀀스_리스트_조회(){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/workspaces/{workspaceId}/sequences",workspaceId)
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

    private EmployeeRequest getEmployeeRequest(){
        EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setName("terry cho");
        employeeRequest.setEmail("8514199@gmail.com");
        employeeRequest.setStartDate("2020-10-09");
        employeeRequest.setWid(workspaceId);
        employeeRequest.setDepartment("back-end");
        employeeRequest.setPhoneNum("010-2431-2298");
        employeeRequest.setManagers(getManagers());

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


    private ManagerRequest getManagerRequest(){
        ManagerRequest managerRequest = new ManagerRequest();
        managerRequest.setWid(workspaceId);
        managerRequest.setDepartment("back-end");
        managerRequest.setName("juram");
        managerRequest.setEmail("8514199@naver.com");
        managerRequest.setPhoneNum("010-3333-2222");
        return managerRequest;
    }

    private ManagerRequest getHrManagerRequest(){
        ManagerRequest managerRequest = new ManagerRequest();
        managerRequest.setWid(workspaceId);
        managerRequest.setDepartment("HR");
        managerRequest.setName("hanna");
        managerRequest.setEmail("rideat63@gmail.com");
        managerRequest.setPhoneNum("010-1111-2222");
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

    private SequenceRequest getModifiedSequenceRequest() {
        SequenceRequest sequenceRequest = new SequenceRequest();
        sequenceRequest.setName("담당 사수와의 미팅");
        sequenceRequest.setDayOffset(-2);
        sequenceRequest.setPrevSequenceId(0L);
        sequenceRequest.setRoleId(directManagerRoleId);

        return sequenceRequest;
    }


    private LaunchRequest getLaunchRequest(){
        LaunchRequest launchRequest = new LaunchRequest();
        launchRequest.setMemberId(employeeId);
        launchRequest.setKeyDate(LocalDate.of(2000,10,9));
        MemberRoleRequest memberRoleRequest = new MemberRoleRequest();
        memberRoleRequest.setMemberId(employeeId);
        memberRoleRequest.setRoleId(employeeRoleId);


        MemberRoleRequest memberRoleRequest2 = new MemberRoleRequest();
        memberRoleRequest2.setMemberId(hrManagerId);
        memberRoleRequest2.setRoleId(hrManagerRoleId);

        MemberRoleRequest memberRoleRequest3 = new MemberRoleRequest();
        memberRoleRequest3.setMemberId(directManagerId);
        memberRoleRequest3.setRoleId(directManagerRoleId);

        List<MemberRoleRequest> list = new ArrayList<>();
        list.add(memberRoleRequest);
        list.add(memberRoleRequest2);
        list.add(memberRoleRequest3);

        launchRequest.setMembers(list);
        return launchRequest;
    }

    private ModuleRequest getModuleRequest(){
        ModuleRequest moduleRequest = new ModuleRequest();
        moduleRequest.setName("hello new employee!");
        moduleRequest.setType(ModuleType.contents);
        moduleRequest.setContent(new ArrayList());
        moduleRequest.setOrder(1);
        return moduleRequest;
    }

    private ModuleRequest getModifiedModuleRequest(){
        ModuleRequest moduleRequest = new ModuleRequest();
        moduleRequest.setName("bye old employee!");
        moduleRequest.setType(ModuleType.contents);
        moduleRequest.setContent(new ArrayList());
        moduleRequest.setOrder(1);
        return moduleRequest;
    }
    private CurrentUser getCurrentUser(){
        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserId(userId);
        currentUser.setUserEmail(userEmail);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(currentUser, null, List.of(new SimpleGrantedAuthority(("USER"))));

        return currentUser;
    }

}



