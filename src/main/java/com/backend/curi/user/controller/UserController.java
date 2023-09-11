package com.backend.curi.user.controller;

import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.controller.dto.UserRequest;
import com.backend.curi.user.controller.dto.UserResponse;
import com.backend.curi.user.repository.entity.User_;
import com.backend.curi.user.service.UserService;

import feign.Response;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

;


import javax.validation.Valid;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;





    @GetMapping
    @Operation(summary = "get user", description = "유저 정보를 반환합니다.")
    public ResponseEntity<UserResponse> getUser() {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = currentUser.getUserId();
        var user = userService.getUserResponseByUserId(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }



    // 회원가입 하고 보내야함 . 유저 디비에 등록
    @PostMapping
    @Operation(summary = "register", description = "유저 정보를 db에 저장합니다. firebase signup 하고 자동로그인하고 일어나는 게 좋을듯!")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid UserRequest userForm) {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = currentUser.getUserId();
        String userName = userForm.getName();

        UserResponse userResponse = userService.dbStore(userId, userName);

        return new ResponseEntity(userResponse, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateUser(@RequestBody @Valid UserRequest userForm){
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = currentUser.getUserId();
        UserResponse updatedUser = userService.updateUser(userId, userForm);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }
//    @PutMapping(value = "/{userId}")
//    @Operation(summary = "update user", description = "유저 정보를 업데이트합니다.")
//    public ResponseEntity<UserResponse> updateUser(@PathVariable String userId, @RequestBody @Valid UserRequest userForm) {
//        User_ existingUser = userService.getUserByUserId(userId);
//
//        existingUser.setEmail(userForm.getEmail());
//
//        UserResponse updatedUser = userService.updateUser(existingUser);
//
//        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
//    }







}
