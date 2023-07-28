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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspaces/{workspaceId}")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> getMemberList (@PathVariable("workspaceId") Long workspaceId, @RequestParam("type")MemberType memberType, Authentication authentication){
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.getMemberList(currentUser, workspaceId, memberType);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/member/manager")
    public ResponseEntity<MemberResponse> createManager(@RequestBody @Validated(ValidationSequence.class) ManagerRequest request,
                                                         Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.createMember(currentUser, MemberType.manager, request);
        return ResponseEntity. status(HttpStatus.OK).body(response);
    }

    @PutMapping("/member/manager/{mid}")
    public ResponseEntity<MemberResponse> modifyManager(@PathVariable("mid") Long memberId,
                                                        @RequestBody @Validated(ValidationSequence.class) ManagerRequest request,
                                                        Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.modifyMember(currentUser, memberId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PostMapping("/member/employee")
    public ResponseEntity<MemberResponse> createEmployee(@RequestBody @Validated(ValidationSequence.class) EmployeeRequest request,
                                                       Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.createMember(currentUser, MemberType.employee , request);
        return ResponseEntity. status(HttpStatus.OK).body(response);
    }
    @PutMapping("/member/employee/{mid}")
    public ResponseEntity<MemberResponse> modifyEmployee(@PathVariable("mid") Long memberId,
                                                         @RequestBody @Validated(ValidationSequence.class) EmployeeRequest request,
                                                         Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.modifyMember(currentUser, memberId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/member/{mid}")
    public ResponseEntity<MemberResponse> deleteMember(@PathVariable("mid") Long memberId,
                                                         Authentication authentication) {
        var currentUser = (CurrentUser) authentication.getPrincipal();
        var response = memberService.deleteMember(currentUser, memberId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
