package com.backend.curi.member.controller;

import com.backend.curi.exception.sequence.ValidationSequence;
import com.backend.curi.member.controller.dto.*;
import com.backend.curi.member.repository.entity.MemberType;
import com.backend.curi.member.service.MemberService;
import com.backend.curi.security.dto.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/employee")
    public ResponseEntity<EmployeeResponse> getEmployee(@RequestParam("wid") Long workspaceId,
                                                         @RequestBody @Validated(ValidationSequence.class) EmployeeRequest employeeRequest,
                                                         Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.getEmployee(currentUser, workspaceId, employeeRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/employees/")
    public ResponseEntity<EmployeeListResponse> getEmployees(@RequestParam("wid") Long workspaceId,
                                                              Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.getEmployeeList(currentUser, workspaceId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PostMapping("/employee")
    public ResponseEntity<EmployeeResponse> createEmployee(@RequestParam("wid") Long workspaceId,
                                                           @RequestBody @Validated(ValidationSequence.class) EmployeeRequest request,
                                                           Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.createEmployee(currentUser, workspaceId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/employee")
    public ResponseEntity<EmployeeResponse> modifyEmployee(@RequestParam("wid") Long workspaceId,
                                                           @RequestBody @Validated(ValidationSequence.class) EmployeeRequest request,
                                                           Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.modifyEmployee(currentUser, workspaceId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/employee")
    public ResponseEntity<EmployeeResponse> deleteEmployee(@RequestParam("wid") Long workspaceId,
                                                           @RequestBody @Validated(ValidationSequence.class) EmployeeRequest request,
                                                           Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.deleteEmployee(currentUser, workspaceId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }



    @GetMapping("/manager")
    public ResponseEntity<ManagerResponse> getEmployee(@RequestParam("wid") Long workspaceId,
                                                       @RequestBody @Validated(ValidationSequence.class) ManagerRequest request,
                                                       Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.getManager(currentUser, workspaceId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/managers/")
    public ResponseEntity<ManagerListResponse> getManagers(@RequestParam("wid") Long workspaceId,
                                                           Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.getManagerList(currentUser, workspaceId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PostMapping("/manager")
    public ResponseEntity<ManagerResponse> createManager(@RequestParam("wid") Long workspaceId,
                                                           @RequestBody @Validated(ValidationSequence.class) ManagerRequest request,
                                                           Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.createManager(currentUser, workspaceId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/manager")
    public ResponseEntity<ManagerResponse> modifyManager(@RequestParam("wid") Long workspaceId,
                                                           @RequestBody @Validated(ValidationSequence.class) ManagerRequest request,
                                                           Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.modifyManager(currentUser, workspaceId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/manager")
    public ResponseEntity<ManagerResponse> deleteManager(@RequestParam("wid") Long workspaceId,
                                                           @RequestBody @Validated(ValidationSequence.class) ManagerRequest request,
                                                           Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.deleteManager(currentUser, workspaceId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/members")
    public ResponseEntity<MemberListResponse> getMemberList (@RequestParam("wid") Long workspaceId, @RequestParam("type")MemberType memberType, Authentication authentication){
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.getMemberList(currentUser, workspaceId, memberType);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/member")
    public ResponseEntity<MemberResponse> createMember(@RequestBody @Validated(ValidationSequence.class) MemberRequest request,
                                                         Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.createMember(currentUser, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }




}
