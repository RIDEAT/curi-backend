package com.backend.curi.member.controller;

import com.backend.curi.exception.sequence.ValidationSequence;
import com.backend.curi.member.controller.dto.*;
import com.backend.curi.member.repository.entity.Manager;
import com.backend.curi.member.service.MemberService;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.user.service.UserService;
import com.backend.curi.userworkspace.service.UserworkspaceService;
import com.backend.curi.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member/{workspaceId}")
public class MemberController {

    private final MemberService memberService;
    private final UserService userService;
    private final UserworkspaceService userworkspaceService;
    private final WorkspaceService workspaceService;

    @GetMapping("/employee")
    public ResponseEntity<EmployeeResponse> getEmployee(@PathVariable Long workspaceId,
                                                         @RequestBody @Validated(ValidationSequence.class) EmployeeRequest employeeRequest,
                                                         Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.getEmployee(currentUser, workspaceId, employeeRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/employees/")
    public ResponseEntity<EmployeeListResponse> getEmployees(@PathVariable Long workspaceId,
                                                              Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.getEmployeeList(currentUser, workspaceId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PostMapping("/employee")
    public ResponseEntity<EmployeeResponse> createEmployee(@PathVariable Long workspaceId,
                                                           @RequestBody @Validated(ValidationSequence.class) EmployeeRequest request,
                                                           Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.createEmployee(currentUser, workspaceId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/employee")
    public ResponseEntity<EmployeeResponse> modifyEmployee(@PathVariable Long workspaceId,
                                                           @RequestBody @Validated(ValidationSequence.class) EmployeeRequest request,
                                                           Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.modifyEmployee(currentUser, workspaceId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/employee")
    public ResponseEntity<EmployeeResponse> deleteEmployee(@PathVariable Long workspaceId,
                                                           @RequestBody @Validated(ValidationSequence.class) EmployeeRequest request,
                                                           Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.deleteEmployee(currentUser, workspaceId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }



    @GetMapping("/manager")
    public ResponseEntity<ManagerResponse> getEmployee(@PathVariable Long workspaceId,
                                                       @RequestBody @Validated(ValidationSequence.class) ManagerRequest request,
                                                       Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.getManager(currentUser, workspaceId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/managers/")
    public ResponseEntity<ManagerListResponse> getManagers(@PathVariable Long workspaceId,
                                                           Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.getManagerList(currentUser, workspaceId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PostMapping("/manager")
    public ResponseEntity<ManagerResponse> createManager(@PathVariable Long workspaceId,
                                                           @RequestBody @Validated(ValidationSequence.class) ManagerRequest request,
                                                           Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.createManager(currentUser, workspaceId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/manager")
    public ResponseEntity<ManagerResponse> modifyManager(@PathVariable Long workspaceId,
                                                           @RequestBody @Validated(ValidationSequence.class) ManagerRequest request,
                                                           Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.modifyManager(currentUser, workspaceId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/manager")
    public ResponseEntity<ManagerResponse> deleteManager(@PathVariable Long workspaceId,
                                                           @RequestBody @Validated(ValidationSequence.class) ManagerRequest request,
                                                           Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.deleteManager(currentUser, workspaceId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
