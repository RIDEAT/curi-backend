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
    public ResponseEntity<List<MemberResponse>> getMemberList (@PathVariable("workspaceId") Long workspaceId, @RequestParam("type")MemberType memberType){
        var response = memberService.getMemberList(workspaceId, memberType);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/members/manager")
    public ResponseEntity<MemberResponse> createManager(@PathVariable("workspaceId") Long workspaceId,
            @RequestBody @Validated(ValidationSequence.class) ManagerRequest request){
        var response = memberService.createMember(MemberType.manager, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/members/manager/{mid}")
    public ResponseEntity<MemberResponse> modifyManager(@PathVariable("workspaceId") Long workspaceId,
                                                        @PathVariable("mid") Long memberId,
                                                        @RequestBody @Validated(ValidationSequence.class) ManagerRequest request) {
        var response = memberService.modifyMember(memberId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PatchMapping("/members/manager/{mid}")
    public ResponseEntity<MemberResponse> updateManager(@PathVariable("workspaceId") Long workspaceId,
                                                        @PathVariable("mid") Long memberId,
                                                         @RequestBody @Validated(ValidationSequence.class) ManagerUpdateRequest request) {
        var response = memberService.updateManager(memberId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/members/employee")
    public ResponseEntity<MemberResponse> createEmployee(@PathVariable("workspaceId") Long workspaceId,
                                                         @RequestBody @Validated(ValidationSequence.class) EmployeeRequest request) {
        var response = memberService.createMember(MemberType.employee , request);
        return ResponseEntity. status(HttpStatus.CREATED).body(response);
    }
    @PutMapping("/members/employee/{mid}")
    public ResponseEntity<MemberResponse> modifyEmployee(@PathVariable("workspaceId") Long workspaceId,
                                                         @PathVariable("mid") Long memberId,
                                                         @RequestBody @Validated(ValidationSequence.class) EmployeeRequest request) {
        var response = memberService.modifyMember(memberId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/members/employee/{mid}")
    public ResponseEntity<MemberResponse> updateEmployee(@PathVariable("workspaceId") Long workspaceId,
                                                         @PathVariable("mid") Long memberId,
                                                         @RequestBody @Validated(ValidationSequence.class) EmployeeUpdateRequest request) {
        var response = memberService.updateEmployee(memberId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/members/{mid}")
    public ResponseEntity<MemberResponse> deleteMember(@PathVariable("workspaceId") Long workspaceId,
                                                       @PathVariable("mid") Long memberId) {
        var response = memberService.deleteMember(memberId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
