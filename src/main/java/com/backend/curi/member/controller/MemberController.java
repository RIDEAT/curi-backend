package com.backend.curi.member.controller;

import com.backend.curi.exception.sequence.ValidationSequence;
import com.backend.curi.member.controller.dto.*;
import com.backend.curi.member.repository.entity.MemberType;
import com.backend.curi.member.service.MemberService;
import com.backend.curi.security.dto.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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

    @PostMapping("/members")
    public ResponseEntity<MemberResponse> createManager(@PathVariable("workspaceId") Long workspaceId,
            @RequestBody @Validated(ValidationSequence.class) MemberRequest request){
        var response = memberService.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/members/csv")
    public ResponseEntity<List<MemberResponse>> createMembers(@PathVariable("workspaceId") Long workspaceId,
            @RequestBody @Validated(ValidationSequence.class) List<MemberRequest> requests){
        var response = memberService.createMembers(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/members/{mid}")
    public ResponseEntity<MemberResponse> modifyManager(@PathVariable("workspaceId") Long workspaceId,
                                                        @PathVariable("mid") Long memberId,
                                                        @RequestBody @Validated(ValidationSequence.class) MemberRequest request) {
        var response = memberService.modifyMember(memberId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PatchMapping("/members/{mid}")
    public ResponseEntity<MemberResponse> updateManager(@PathVariable("workspaceId") Long workspaceId,
                                                        @PathVariable("mid") Long memberId,
                                                         @RequestBody @Validated(ValidationSequence.class) MemberUpdateRequest request) {
        var response = memberService.updateMember(memberId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/members/{mid}")
    public ResponseEntity<MemberResponse> deleteMember(@PathVariable("workspaceId") Long workspaceId,
                                                       @PathVariable("mid") Long memberId) throws DataIntegrityViolationException {
        var response = memberService.deleteMember(memberId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
