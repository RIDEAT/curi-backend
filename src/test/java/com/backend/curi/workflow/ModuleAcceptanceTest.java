package com.backend.curi.workflow;


import com.backend.curi.common.Constants;
import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.member.controller.dto.EmployeeManagerDetail;
import com.backend.curi.member.controller.dto.EmployeeRequest;
import com.backend.curi.member.controller.dto.ManagerRequest;
import com.backend.curi.member.repository.entity.MemberType;
import com.backend.curi.member.service.MemberService;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.service.UserService;
import com.backend.curi.workflow.controller.dto.*;
import com.backend.curi.workflow.repository.ContentRepository;
import com.backend.curi.workflow.repository.entity.Content;
import com.backend.curi.workflow.repository.entity.ModuleType;
import com.backend.curi.workflow.service.ModuleService;
import com.backend.curi.workflow.service.SequenceService;
import com.backend.curi.workflow.service.WorkflowService;
import com.backend.curi.workspace.controller.dto.WorkspaceRequest;
import com.backend.curi.workspace.controller.dto.WorkspaceResponse;
import com.backend.curi.workspace.service.RoleService;
import com.backend.curi.workspace.service.WorkspaceService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-data.properties")
public class ModuleAcceptanceTest {



    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private WorkspaceService workspaceService;
    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private SequenceService sequenceService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private RoleService roleService;
    @Autowired
    private MemberService memberService;

    @LocalServerPort
    public int port;

    private final String userId = Constants.userId;
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

    private Long sequenceId;
    private Long templateModuleId;

    private Long sequenceInWorkflowId;

    private Long moduleInSequenceId;

    private Long defaultRoleId;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;

        userService.dbStore(userId, userEmail);
        WorkspaceResponse workspaceResponse = workspaceService.createWorkspace(getWorkspaceRequest(), getCurrentUser());
        WorkspaceResponse workspaceResponse2 = workspaceService.createWorkspace(getWorkspaceRequest(), getCurrentUser());

        workspaceId = workspaceResponse.getId();
        workspaceId2 = workspaceResponse2.getId();
        defaultRoleId = workspaceResponse.getRoles().get(1).getId();

        var managerResponse = memberService.createMember(getCurrentUser(), MemberType.manager, getManagerRequest());

        managerId = managerResponse.getId();

        var employeeResponse = memberService.createMember(getCurrentUser(), MemberType.employee, getEmployeeRequest());

        employeeId = employeeResponse.getId();


        var workflowResponse = workflowService.createWorkflow(workspaceId, getWorkflowRequest());
        var sequence = sequenceService.createSequence(workspaceId, getSequenceRequest());

        workflowId = workflowResponse.getId();
        sequenceId = sequence.getId();

        var sequenceInWorkflow = sequenceService.createSequence(workspaceId, workflowId,getSequenceRequest());
        sequenceInWorkflowId = sequenceInWorkflow.getId();

        var module = moduleService.createModule(workspaceId, getModuleRequest());
        templateModuleId = module.getId();

        var moduleInSequence = moduleService.createModule(workspaceId, sequenceInWorkflowId, getModuleRequest());
        moduleInSequenceId = moduleInSequence.getId();
    }

    @DisplayName("워크스페이스에 속한 모듈 리스트를 조회할 수 있다.")
    @Test
    public void getModules(){
        ExtractableResponse<Response> response = 워크스페이스내_모듈_리스트_조회(workspaceId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());


        ExtractableResponse<Response> sequenceResponse = 워크스페이스내_시퀀스_조회(sequenceInWorkflowId);
        assertThat(sequenceResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }


    @DisplayName("워크스페이스에 속한 모듈을 조회할 수 있다.")
    @Test
    public void getModule(){
        ExtractableResponse<Response> response = 워크스페이스내_모듈_조회(workspaceId, templateModuleId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }


    @DisplayName("워크스페이스에 속하지 않은 모듈을 조회할 수 없다.")
    @Test
    public void getModuleInOtherWorkspace(){
        ExtractableResponse<Response> response = 워크스페이스내_모듈_조회(workspaceId2, templateModuleId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("워크스페이스 내에 템플릿 모듈을 생성할 수 있다.")
    @Test
    public void createModule(){
        ExtractableResponse<Response> response = 모듈_생성(getModuleRequest());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        ModuleResponse moduleResponse = response.as(ModuleResponse.class);
        System.out.println(moduleResponse.getContentId());


        String str = contentRepository.findById(moduleResponse.getContentId()).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.MODULE_NOT_EXISTS)).getContent().toString();

        System.out.println(contentRepository.findById(moduleResponse.getContentId()).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.MODULE_NOT_EXISTS)).getContent().toString());

        Map<String, String> substitutions = Map.of("employee", "jiseung");

        String substitedString =  substitutePlaceholders(str, substitutions);

        System.out.println(substitedString);

    }

    @DisplayName("시퀀스 내 모듈을 추가할 수 있다.")
    @Test
    public void createSequenceInWorkflow(){
        ExtractableResponse<Response> moduleResponse = 시퀀스내_모듈_생성(getModuleRequest());
        assertThat(moduleResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    }


    @DisplayName("모듈을 수정할 수 있다.")
    @Test
    public void updateSequence(){
        ExtractableResponse<Response> updateResponse = 모듈_수정(getModifiedModuleRequest());
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }


    @DisplayName("시퀀스 내 모듈을 수정할 수 있다. (순서 변경)")
    @Test
    public void updateSequenceInWorkflow(){
        ExtractableResponse<Response> updatedResponse = 시퀀스내_모듈_수정(getModifiedModuleRequest());
        assertThat(updatedResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

    }

    @DisplayName("워크 스페이스 내에 템플릿 모듈을 삭제할 수 있다.")
    @Test
    public void deleteModule(){
        ExtractableResponse<Response> response = 모듈_삭제();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

    }

    @DisplayName("워크플로우 아래에 시퀀스 내에 모듈을 삭제할 수 있다.")
    @Test
    public void deleteModuleInSequence(){
        ExtractableResponse<Response> response = 시퀀스내_모듈_삭제();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 워크스페이스내_시퀀스_조회(Long sequenceId){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/workspaces/{workspaceId}/sequences/{sequenceId}",workspaceId, sequenceId)
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
    private ExtractableResponse<Response> 워크스페이스내_모듈_리스트_조회(Long workspaceId){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/workspaces/{workspaceId}/modules",workspaceId)
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 워크스페이스내_모듈_조회(Long workspaceId, Long moduleId){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/workspaces/{workspaceId}/modules/{moduleId}",workspaceId, moduleId)
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 모듈_생성(ModuleRequest moduleRequest){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .body(moduleRequest)
                .when()
                .post("/workspaces/{workspaceId}/modules", workspaceId)
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 시퀀스내_모듈_생성(ModuleRequest moduleRequest){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .body(moduleRequest)
                .when()
                .post("/workspaces/{workspaceId}/sequences/{sequenceId}/modules", workspaceId, sequenceInWorkflowId)
                .then()
                .log()
                .all()
                .extract();
    }



    private ExtractableResponse<Response> 모듈_수정(ModuleRequest moduleRequest){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .body(moduleRequest)
                .when()
                .put("/workspaces/{workspaceId}/modules/{moduleId}", workspaceId, templateModuleId)
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 시퀀스내_모듈_수정(ModuleRequest moduleRequest){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .body(moduleRequest)
                .when()
                .put("/workspaces/{workspaceId}/sequences/{sequenceId}/modules/{moduleId}", workspaceId, sequenceInWorkflowId, moduleInSequenceId)
                .then()
                .log()
                .all()
                .extract();
    }


    private ExtractableResponse<Response> 모듈_삭제(){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .when()
                .delete("/workspaces/{workspaceId}/modules/{moduleId}", workspaceId, templateModuleId)
                .then()
                .log()
                .all()
                .extract();
    }

    private ExtractableResponse<Response> 시퀀스내_모듈_삭제(){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .when()
                .delete("/workspaces/{workspaceId}/sequences/{sequenceId}/modules/{moduleId}", workspaceId, sequenceInWorkflowId, moduleInSequenceId)
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
        employeeRequest.setEmail("terry@gmail.com");
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
        employeeManagerDetail.setId(managerId);
        employeeManagerDetail.setName("juram");
        employeeManagerDetail.setRoleId(defaultRoleId);
        employeeManagerDetail.setRoleName("담당 사수");
        employeeManagerDetails.add(employeeManagerDetail);
        return employeeManagerDetails;
    }


    private ManagerRequest getManagerRequest(){
        ManagerRequest managerRequest = new ManagerRequest();
        managerRequest.setWid(workspaceId);
        managerRequest.setDepartment("back-end");
        managerRequest.setName("juram");
        managerRequest.setEmail("juram@gmail.com");
        managerRequest.setPhoneNum("010-3333-2222");
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
        sequenceRequest.setRoleId(defaultRoleId);

        return sequenceRequest;
    }

    private SequenceRequest getModifiedSequenceRequest() {
        SequenceRequest sequenceRequest = new SequenceRequest();
        sequenceRequest.setName("담당 사수와의 미팅");
        sequenceRequest.setDayOffset(-2);
        sequenceRequest.setPrevSequenceId(0L);
        sequenceRequest.setRoleId(defaultRoleId);

        return sequenceRequest;
    }

    private ModuleRequest getModuleRequest(){
        ModuleRequest moduleRequest = new ModuleRequest();
        moduleRequest.setName("hello new employee!");
        moduleRequest.setType(ModuleType.contents);
        moduleRequest.setContent("{ \"type\" : \"contents\", \"content\" : \"안녕하세요 {신규입사자} nice to meet you!\" }");
        moduleRequest.setOrder(1);
        return moduleRequest;
    }

    private ModuleRequest getModifiedModuleRequest(){
        ModuleRequest moduleRequest = new ModuleRequest();
        moduleRequest.setName("bye old employee!");
        moduleRequest.setType(ModuleType.contents);
        moduleRequest.setContent("{ \"type\" : \"contents\", \"content\" : \"niece {employee} nice to meet you!\" }");
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

    public static String substitutePlaceholders(String jsonString, Map<String, String> substitutions) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            if (jsonNode.isObject()) {
                JsonNode contentNode = jsonNode.get("content");
                if (contentNode != null && contentNode.isTextual()) {
                    String content = contentNode.textValue();
                    for (Map.Entry<String, String> entry : substitutions.entrySet()) {
                        String placeholder = "{" + entry.getKey() + "}";
                        content = content.replace(placeholder, entry.getValue());
                    }
                    ((com.fasterxml.jackson.databind.node.ObjectNode) jsonNode).put("content", content);
                }
            }

            return jsonNode.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}



