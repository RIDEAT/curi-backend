package com.backend.curi.user.controller;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.controller.dto.UserForm;
import com.backend.curi.user.repository.entity.User_;
import com.backend.curi.user.service.UserService;

import com.backend.curi.userworkspace.repository.entity.Userworkspace;
import com.backend.curi.userworkspace.service.UserworkspaceService;
import com.backend.curi.workspace.repository.entity.Workspace;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

import static com.backend.curi.security.configuration.Constants.AUTH_SERVER;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserworkspaceService userworkspaceService;

    @GetMapping(value = "/{workspaceId}")
    @Operation(summary = "get user List", description = "워크스페이스 내의 유저리스트를 반환합니다.",
            parameters = {
                    @Parameter(
                            name = "refreshToken",
                            in = ParameterIn.COOKIE,
                            schema = @Schema(implementation = String.class)
                    )
            })
    @SecurityRequirement(name = "Auth-token")
    public ResponseEntity getUserList(@PathVariable int workspaceId, Authentication authentication) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("transactionId", 11);

        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        String userId = currentUser.getUserId();
        //String userEmail = currentUser.getUserEmail();

        List<String> userIdList = userworkspaceService.getUserIdListByWorkspaceId(workspaceId);

        if (!userIdList.contains(userId)) {
            throw new CuriException(HttpStatus.FORBIDDEN, ErrorType.UNAUTHORIZED_WORKSPACE);
        }

        // 비웠을 때는 따로 예외처리 해주어야 하나.
        // 헤더에 auth 토큰 넣어야 하는데.

        List<User_> userList = convertToUser(userIdList);
        responseBody.put("user list", userList);

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }


    // 회원가입 하고 보내야함 . 유저 디비에 등록
    @PostMapping
    @Operation(summary = "register", description = "유저 정보를 db에 저장합니다. firebase signup 하고 자동로그인하고 일어나는 게 좋을듯!",
            parameters = {
                    @Parameter(
                            name = "refreshToken",
                            in = ParameterIn.COOKIE,
                            schema = @Schema(implementation = String.class)
                    )
            })
    @SecurityRequirement(name = "Auth-token")
    public ResponseEntity register(Authentication authentication, @RequestBody UserForm userForm, HttpServletRequest request, HttpServletResponse response) {
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        String userId = currentUser.getUserId();
        String userEmail = userForm.getEmail();


        userService.dbStore(userId, userEmail);


        Map<String, Object> responseBodyMap = new HashMap<>();
        responseBodyMap.put("userId", userId);
        responseBodyMap.put("userEmail", userEmail);


        return new ResponseEntity(responseBodyMap, HttpStatus.ACCEPTED);
    }

    @PutMapping(value = "/{userId}")
    @Operation(summary = "update user", description = "유저 정보를 업데이트합니다.",
            parameters = {
                    @Parameter(
                            name = "refreshToken",
                            in = ParameterIn.COOKIE,
                            schema = @Schema(implementation = String.class)
                    )
            })
    @SecurityRequirement(name = "Auth-token")
    public ResponseEntity updateUser(@PathVariable String userId, @RequestBody UserForm userForm) {
        User_ existingUser = userService.getUserByUserId(userId);

        // Update the necessary fields of the existing user

        existingUser.setEmail(userForm.getEmail());

        User_ updatedUser = userService.updateUser(existingUser);

        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{userId}")
    @Operation(summary = "delete user", description = "유저를 삭제합니다.",
            parameters = {
                    @Parameter(
                            name = "refreshToken",
                            in = ParameterIn.COOKIE,
                            schema = @Schema(implementation = String.class)
                    ),
                    @Parameter(
                            name = "userId",
                            in = ParameterIn.PATH,
                            description = "삭제할 유저의 ID",
                            schema = @Schema(implementation = String.class)
                    )
            })
    @SecurityRequirement(name = "Auth-token")
    public ResponseEntity deleteUser(@PathVariable String userId, Authentication authentication) {
        User_ existingUser = userService.getUserByUserId(userId);

        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        String currentUserUserId = currentUser.getUserId();
        //String userEmail = currentUser.getUserEmail();

        if (!currentUserUserId.equals(userId))
            throw new CuriException(HttpStatus.FORBIDDEN, ErrorType.UNAUTHORIZED_USER);

        userService.deleteUser(existingUser);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "User deleted successfully.");

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    private List<User_> convertToUser(List<String> userIdList) {
        List<User_> userList = new ArrayList<>();
        for (String userId : userIdList) {
            userList.add(userService.getUserByUserId(userId));
        }
        return userList;
    }


}
