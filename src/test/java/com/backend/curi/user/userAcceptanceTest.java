package com.backend.curi.user;




import com.backend.curi.common.Constants;
import com.backend.curi.common.feign.SchedulerOpenFeign;
import com.backend.curi.member.controller.dto.EmployeeManagerDetail;
import com.backend.curi.member.service.MemberService;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.slack.controller.dto.SlackMessageRequest;
import com.backend.curi.slack.service.SlackService;
import com.backend.curi.user.controller.dto.UserRequest;
import com.backend.curi.user.repository.UserRepository;
import com.backend.curi.user.service.UserService;
import com.backend.curi.workflow.controller.dto.*;
import com.backend.curi.workflow.repository.entity.ModuleType;
import com.backend.curi.workflow.service.ModuleService;
import com.backend.curi.workflow.service.SequenceService;
import com.backend.curi.workflow.service.WorkflowService;
import com.backend.curi.workspace.controller.dto.WorkspaceRequest;
import com.backend.curi.workspace.service.RoleService;
import com.backend.curi.workspace.service.WorkspaceService;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
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
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-data.properties")
public class userAcceptanceTest {


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
    private UserRepository userRepository;

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

    private Long sequenceInWorkflowId;

    private Long employeeRoleId;
    private Long directManagerRoleId;
    private Long hrManagerRoleId;

    private Long templateModuleId;
    private Long moduleInSequenceId;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;


    }

    @DisplayName("유저를 등록할 수 있다. ")
    @Test
    public void getRequiredRoles(){
      ExtractableResponse<Response> response = 유저_생성();
      assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }



    private ExtractableResponse<Response>유저_생성(){
        return RestAssured.
                given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON) // JSON 형식으로 request body를 설정
                .body(getUserRequest())
                .when()
                .post("/user")
                .then()
                .log()
                .all()
                .extract();
    }

    private UserRequest getUserRequest(){
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("8514199@gmail.com");
        userRequest.setName("jiseung");
        return userRequest;
    }



    private ResponseEntity communicateWithAuthServer(HttpServletRequest request) {
        RestTemplate restTemplate = new RestTemplate();

        HttpMethod httpMethod = HttpMethod.GET; // 호출할 HTTP 메서드 선택 (GET, POST, 등)
        URI requestUri = URI.create(Constants.AUTH_SERVER.concat("/verify"));
        HttpHeaders requestHeaders = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            requestHeaders.add(headerName, headerValue);
        }
        RequestEntity<Void> requestEntity = new RequestEntity<>(requestHeaders, httpMethod, requestUri);

        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        return responseEntity;
    }

}



