package com.backend.curi.member.controller;

import com.backend.curi.exception.sequence.ValidationSequence;
import com.backend.curi.member.controller.dto.EmployeeListResponse;
import com.backend.curi.member.controller.dto.EmployeeRequest;
import com.backend.curi.member.controller.dto.EmployeeResponse;
import com.backend.curi.member.service.MemberService;
import com.backend.curi.security.dto.CurrentUser;
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
@Slf4j
@RequestMapping("/member/{workspaceId}")
public class MemberController {

    private final MemberService memberService;


    @GetMapping("/employee")
    public ResponseEntity<EmployeeResponse> getEmployee(@PathVariable int workspaceId,
                                                         @Validated(ValidationSequence.class) EmployeeRequest employeeRequest,
                                                         Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.getEmployee(currentUser, workspaceId, employeeRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/employees/")
    public ResponseEntity<EmployeeListResponse> getEmployees(@PathVariable int workspaceId,
                                                              Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.getEmployeeList(currentUser, workspaceId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PostMapping("/employee")
    public ResponseEntity<EmployeeResponse> createEmployee(@PathVariable int workspaceId,
                                                           @Validated(ValidationSequence.class) EmployeeRequest employeeRequest,
                                                           Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.createEmployee(currentUser, workspaceId, employeeRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/employee")
    public ResponseEntity<EmployeeResponse> modifyEmployee(@PathVariable int workspaceId,
                                                           @Validated(ValidationSequence.class) EmployeeRequest employeeRequest,
                                                           Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.modifyEmployee(currentUser, workspaceId, employeeRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/employee")
    public ResponseEntity<EmployeeResponse> deleteEmployee(@PathVariable int workspaceId,
                                                           @Validated(ValidationSequence.class) EmployeeRequest employeeRequest,
                                                           Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.deleteEmployee(currentUser, workspaceId, employeeRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
