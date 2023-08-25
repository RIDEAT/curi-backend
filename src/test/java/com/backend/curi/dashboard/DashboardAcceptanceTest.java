//package com.backend.curi.dashboard;
//
//
//import com.backend.curi.common.Constants;
//import com.backend.curi.common.feign.SchedulerOpenFeign;
//import com.backend.curi.common.feign.dto.SequenceMessageRequest;
//import com.backend.curi.launched.controller.dto.LaunchedWorkflowRequest;
//import com.backend.curi.launched.controller.dto.LaunchedWorkflowResponse;
//import com.backend.curi.launched.repository.entity.LaunchedStatus;
//import com.backend.curi.member.controller.dto.EmployeeManagerDetail;
//import com.backend.curi.member.controller.dto.EmployeeRequest;
//import com.backend.curi.member.controller.dto.ManagerRequest;
//import com.backend.curi.member.repository.entity.MemberType;
//import com.backend.curi.member.service.MemberService;
//import com.backend.curi.security.dto.CurrentUser;
//import com.backend.curi.slack.controller.dto.SlackMessageRequest;
//import com.backend.curi.slack.service.SlackService;
//import com.backend.curi.user.service.UserService;
//import com.backend.curi.workflow.controller.dto.*;
//import com.backend.curi.workflow.repository.entity.ModuleType;
//import com.backend.curi.workflow.service.LaunchService;
//import com.backend.curi.workflow.service.ModuleService;
//import com.backend.curi.workflow.service.SequenceService;
//import com.backend.curi.workflow.service.WorkflowService;
//import com.backend.curi.workspace.controller.dto.WorkspaceRequest;
//import com.backend.curi.workspace.controller.dto.WorkspaceResponse;
//import com.backend.curi.workspace.service.RoleService;
//import com.backend.curi.workspace.service.WorkspaceService;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.slack.api.methods.response.chat.ChatPostMessageResponse;
//import io.restassured.response.ExtractableResponse;
//import io.restassured.response.Response;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.BeforeEach;
//import io.restassured.RestAssured;
//
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.Mockito.*;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.test.context.TestPropertySource;
//
//@ExtendWith(MockitoExtension.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource("classpath:application-data.properties")
//public class DashboardAcceptanceTest {
//
//    @MockBean
//    private SchedulerOpenFeign schedulerOpenFeign;
//
//    @MockBean
//    private SlackService slackService;
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private WorkspaceService workspaceService;
//    @Autowired
//    private WorkflowService workflowService;
//
//    @Autowired
//    private SequenceService sequenceService;
//
//    @Autowired
//    private RoleService roleService;
//    @Autowired
//    private MemberService memberService;
//
//    @Autowired
//    private ModuleService moduleService;
//
//    @Autowired
//    private LaunchService launchService;
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
//    private Long workspaceId2;
//    private Long employeeId;
//
//    private Long directManagerId;
//
//    private Long hrManagerId;
//    private Long managerId;
//
//    private Long workflowId;
//
//    private Long sequenceId;
//
//    private Long defaultRoleId;
//
//    private Long employeeRoleId;
//
//    private Long directManagerRoleId;
//    private Long hrManagerRoleId;
//
//    private Long launchedworkflowId;
//
//    private Long templateModuleId;
//    private Long moduleInSequenceId;
//
//
//
//    @BeforeEach
//    public void setup() throws JsonProcessingException {
//        defaultSet();
//        userMakeWorkspace();
//        userMakeEmployeeAndManager();
//        userMakeWorkspaceSequenceModule();
//        userLaunchWorkflow();
//    }
//
//    private void defaultSet (){
//        when(schedulerOpenFeign.createMessage(any(SequenceMessageRequest.class)))
//                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());
//
//        when(schedulerOpenFeign.deleteMessage(any(Long.class)))
//                .thenReturn(ResponseEntity.noContent().build());
//
//        when(slackService.sendMessage(any(SlackMessageRequest.class)))
//                .thenReturn(new ChatPostMessageResponse());
//
//
//        RestAssured.port = port;
//
//    }
//
//
//    private void userMakeWorkspace(){
//        userService.dbStore(userId, userEmail);
//        WorkspaceResponse workspaceResponse = workspaceService.createWorkspace(getWorkspaceRequest(), getCurrentUser());
//        WorkspaceResponse workspaceResponse2 = workspaceService.createWorkspace(getWorkspaceRequest(), getCurrentUser());
//        workspaceId = workspaceResponse.getId();
//        workspaceId2= workspaceResponse2.getId();
//        employeeRoleId = workspaceResponse.getRoles().get(0).getId();
//        directManagerRoleId = workspaceResponse.getRoles().get(1).getId();
//        hrManagerRoleId = workspaceResponse.getRoles().get(2).getId();    }
//
//    private void userMakeEmployeeAndManager(){
//        var managerResponse = memberService.createMember(MemberType.manager, getManagerRequest());
//
//        directManagerId= managerResponse.getId();
//
//        var hrManagerResponse = memberService.createMember(MemberType.manager, getHrManagerRequest());
//
//        hrManagerId = hrManagerResponse.getId();
//
//        var employeeResponse = memberService.createMember(MemberType.employee, getEmployeeRequest());
//
//        employeeId = employeeResponse.getId();
//    }
//
//    private void userMakeWorkspaceSequenceModule(){
//        var workflowResponse = workflowService.createWorkflow(workspaceId, getWorkflowRequest());
//        workflowId = workflowResponse.getId();
//
//        var sequence = sequenceService.createSequence(workspaceId, workflowId,getSequenceRequest());
//        sequenceId = sequence.getId();
//
//        var module = moduleService.createModule(workspaceId,sequenceId, getModuleRequest());
//        templateModuleId = module.getId();
//
//        var moduleInSequence = moduleService.createModule(workspaceId, sequenceId, getModuleRequest());
//        moduleInSequenceId = moduleInSequence.getId();
//    }
//
//    private void userLaunchWorkflow() throws JsonProcessingException {
//        //securityContext 설정
//        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
//        Authentication authentication = Mockito.mock(Authentication.class);
//        SecurityContextHolder.setContext(securityContext);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getPrincipal()).thenReturn(getCurrentUser());
//
//        var launchedWorkflow = launchService.launchWorkflow(workflowId, getLaunchRequest(), workspaceId);
//        launchedworkflowId = launchedWorkflow.getId();
//    }
//
//
//    @DisplayName("대시보드에서 템플릿 워크플로우 별로 현황을 볼 수 있다.")
//    @Test
//    public void getDashboardWorkflow(){
//        ExtractableResponse<Response> response = 대시보드_워크플로우_조회();
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
//    }
//
//    /*
//    @DisplayName("대시보드에서 템플릿 워크플로우 별로 멤버 현황을 볼 수 있다.")
//    @Test
//    public void getDashboardMember(){
//        ExtractableResponse<Response> response = 대시보드_멤버_조회();
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
//    }
//
//*/
//
//
//    private ExtractableResponse<Response> 대시보드_워크플로우_조회() {
//        return RestAssured.
//                given()
//                .header("Authorization", "Bearer " + authToken)
//                .when()
//                .get("/workspaces/{workspaceId}/dashboard/workflows",workspaceId)
//                .then()
//                .log()
//                .all()
//                .extract();
//
//    }
//
//    private ExtractableResponse<Response> 대시보드_멤버_조회() {
//        return RestAssured.
//                given()
//                .header("Authorization", "Bearer " + authToken)
//                .when()
//                .get("/workspaces/{workspaceId}/dashboard/workflows/{workflowId}/members",workspaceId, 695)
//                .then()
//                .log()
//                .all()
//                .extract();
//    }
//
//    private WorkspaceRequest getWorkspaceRequest(){
//        return new WorkspaceRequest(workspaceName, workspaceEmail);
//    }
//
//    private LaunchedWorkflowRequest getModifiedLaunchedWorkflowRequest(LaunchedWorkflowResponse originalLaunchedWorkflowResponse){
//        LaunchedWorkflowRequest workflowRequest = new LaunchedWorkflowRequest();
//        workflowRequest.setWorkflowId(workflowId);
//        workflowRequest.setStatus(LaunchedStatus.IN_PROGRESS);
//        workflowRequest.setKeyDate(originalLaunchedWorkflowResponse.getKeyDate());
//        workflowRequest.setName(originalLaunchedWorkflowResponse.getName());
//        return workflowRequest;
//    }
//    private EmployeeRequest getEmployeeRequest(){
//        EmployeeRequest employeeRequest = new EmployeeRequest();
//        employeeRequest.setName("terry cho");
//        employeeRequest.setEmail("terry@gmail.com");
//        employeeRequest.setStartDate("2020-10-09");
//        employeeRequest.setWid(workspaceId);
//        employeeRequest.setDepartment("back-end");
//        employeeRequest.setPhoneNum("010-2431-2298");
//        employeeRequest.setManagers(getManagers());
//
//        return employeeRequest;
//    }
//
//    private List<EmployeeManagerDetail> getManagers(){
//        List<EmployeeManagerDetail> employeeManagerDetails = new ArrayList<>();
//        EmployeeManagerDetail employeeManagerDetail = new EmployeeManagerDetail();
//        employeeManagerDetail.setId(directManagerId);
//        employeeManagerDetail.setName("juram");
//        employeeManagerDetail.setRoleId(directManagerRoleId);
//        employeeManagerDetail.setRoleName("담당사수");
//        employeeManagerDetails.add(employeeManagerDetail);
//
//        EmployeeManagerDetail employeeManagerDetail2 = new EmployeeManagerDetail();
//        employeeManagerDetail2.setId(hrManagerId);
//        employeeManagerDetail2.setName("hanna");
//        employeeManagerDetail2.setRoleId(hrManagerRoleId);
//        employeeManagerDetail2.setRoleName("hr매니저");
//        employeeManagerDetails.add(employeeManagerDetail2);
//
//        return employeeManagerDetails;
//    }
//
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
//
//    private ManagerRequest getHrManagerRequest(){
//        ManagerRequest managerRequest = new ManagerRequest();
//        managerRequest.setWid(workspaceId);
//        managerRequest.setDepartment("HR");
//        managerRequest.setName("hanna");
//        managerRequest.setEmail("hanna@gmail.com");
//        managerRequest.setPhoneNum("010-1111-2222");
//        return managerRequest;
//    }
//
//
//    private WorkflowRequest getWorkflowRequest(){
//        WorkflowRequest workflowRequest = new WorkflowRequest();
//        workflowRequest.setName(workflowName);
//        return workflowRequest;
//    }
//
//
//    private SequenceRequest getSequenceRequest() {
//        SequenceRequest sequenceRequest = new SequenceRequest();
//        sequenceRequest.setName("신입 환영 시퀀스");
//        sequenceRequest.setDayOffset(-2);
//        sequenceRequest.setPrevSequenceId(0L);
//        sequenceRequest.setRoleId(hrManagerRoleId);
//
//        return sequenceRequest;
//    }
//
//    private SequenceRequest getModifiedSequenceRequest() {
//        SequenceRequest sequenceRequest = new SequenceRequest();
//        sequenceRequest.setName("담당 사수와의 미팅");
//        sequenceRequest.setDayOffset(-2);
//        sequenceRequest.setPrevSequenceId(0L);
//        sequenceRequest.setRoleId(directManagerRoleId);
//
//        return sequenceRequest;
//    }
//
//
//    private LaunchRequest getLaunchRequest(){
//        LaunchRequest launchRequest = new LaunchRequest();
//        launchRequest.setMemberId(employeeId);
//        launchRequest.setKeyDate(LocalDate.of(2000,10,9));
//        return launchRequest;
//    }
//
//    private ModuleRequest getModuleRequest(){
//        ModuleRequest moduleRequest = new ModuleRequest();
//        moduleRequest.setName("hello new employee!");
//        moduleRequest.setType(ModuleType.contents);
//        moduleRequest.setContent(new ArrayList());
//        moduleRequest.setOrder(1);
//        return moduleRequest;
//    }
//
//    private ModuleRequest getModifiedModuleRequest(){
//        ModuleRequest moduleRequest = new ModuleRequest();
//        moduleRequest.setName("bye old employee!");
//        moduleRequest.setType(ModuleType.contents);
//        moduleRequest.setContent(new ArrayList());
//        moduleRequest.setOrder(1);
//        return moduleRequest;
//    }
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
