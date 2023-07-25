package com.backend.curi.launchedsequence.controller;

import com.backend.curi.launchedsequence.controller.dto.LaunchedSequenceRequest;
import com.backend.curi.launchedsequence.controller.dto.LaunchedSequenceResponse;
import com.backend.curi.launchedsequence.service.LaunchedSequenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workspaces/{workspaceId}/launchedworkflows/{launchedworkflowId}")
// 앞 단에서 공통적으로 workspace에 접근 권한을 확인
@RequiredArgsConstructor
public class LaunchedSequenceController {

    private final LaunchedSequenceService launchedSequenceService;

    @GetMapping("/sequences/{sequenceId}")
    public ResponseEntity<LaunchedSequenceResponse> getLaunchedSequence(@PathVariable Long workspaceId, @PathVariable Long launchedworkflowId, @PathVariable Long sequenceId) {
        LaunchedSequenceResponse launchedSequence = launchedSequenceService.getLaunchedSequence(sequenceId);
        return ResponseEntity.ok(launchedSequence);
    }



    @PostMapping("/sequences")
    public ResponseEntity<LaunchedSequenceResponse> createLaunchedSequence(@PathVariable Long workspaceId,@PathVariable Long launchedworkflowId, @RequestBody LaunchedSequenceRequest launchedSequenceRequest) {
        LaunchedSequenceResponse createdLaunchedSequence = launchedSequenceService.createLaunchedSequence(launchedSequenceRequest);
        return ResponseEntity.ok(createdLaunchedSequence);
    }
}
