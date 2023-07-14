package com.backend.curi.user.controller;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.controller.dto.UserRequest;
import com.backend.curi.user.controller.dto.UserResponse;
import com.backend.curi.user.repository.entity.User_;
import com.backend.curi.user.service.UserService;

import com.backend.curi.userworkspace.service.UserworkspaceService;
import com.backend.curi.workspace.repository.entity.Workspace;
import com.backend.curi.workspace.service.WorkspaceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/{workspaceId}")
    @Operation(summary = "get user List", description = "워크스페이스 내의 유저리스트를 반환합니다.")
    public ResponseEntity getUserList(@PathVariable int workspaceId) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("transactionId", 11);

        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = currentUser.getUserId();
        //String userEmail = currentUser.getUserEmail();



        var userList = userService.getAllUsers(workspaceId, currentUser);
        // 비웠을 때는 따로 예외처리 해주어야 하나.
        // 헤더에 auth 토큰 넣어야 하는데.

        responseBody.put("user list", userList);

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }




    // 회원가입 하고 보내야함 . 유저 디비에 등록
    @PostMapping
    @Operation(summary = "register", description = "유저 정보를 db에 저장합니다. firebase signup 하고 자동로그인하고 일어나는 게 좋을듯!")
    public ResponseEntity register(@RequestBody UserRequest userForm) {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = currentUser.getUserId();
        String userEmail = userForm.getEmail();

        UserResponse userResponse = userService.dbStore(userId, userEmail);

        return new ResponseEntity(userResponse, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{userId}")
    @Operation(summary = "update user", description = "유저 정보를 업데이트합니다.")
    public ResponseEntity updateUser(@PathVariable String userId, @RequestBody UserRequest userForm) {
        User_ existingUser = userService.getUserByUserId(userId);

        checkIfuserHasAuth(userId);

        existingUser.setEmail(userForm.getEmail());

        UserResponse updatedUser = userService.updateUser(existingUser);

        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{userId}")
    @Operation(summary = "delete user", description = "유저를 삭제합니다.")
    public ResponseEntity deleteUser(@PathVariable String userId) {
        User_ existingUser = userService.getUserByUserId(userId);

        checkIfuserHasAuth(userId);

        UserResponse deletedUser = userService.deleteUser(existingUser);

        return new ResponseEntity<>(deletedUser, HttpStatus.OK);
    }

    private List<UserResponse> convertToUserResponse(List<String> userIdList) {
        List<UserResponse> userResponseList = new ArrayList<>();
        for (String userId : userIdList) {
            userResponseList.add(userService.getUserResponseByUserId(userId));
        }

        return userResponseList;

    }

    private void checkIfuserHasAuth(String userId){
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUserUserId = currentUser.getUserId();
        //String userEmail = currentUser.getUserEmail();

        if (!currentUserUserId.equals(userId))
            throw new CuriException(HttpStatus.FORBIDDEN, ErrorType.UNAUTHORIZED_USER);
    }


}
