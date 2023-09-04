package com.backend.curi.launched.controller;

import com.backend.curi.exception.sequence.ValidationSequence;
import com.backend.curi.launched.controller.dto.LaunchedSequenceRequest;
import com.backend.curi.launched.controller.dto.LaunchedSequenceResponse;
import com.backend.curi.launched.controller.dto.LaunchedSequenceUpdateRequest;
import com.backend.curi.launched.service.LaunchedSequenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workspaces/{workspaceId}/launchedworkflows/{launchedworkflowId}/sequences")
@RequiredArgsConstructor
public class LaunchedSequenceController {

    private final LaunchedSequenceService launchedSequenceService;

    @GetMapping("/{sequenceId}")
    public ResponseEntity<LaunchedSequenceResponse> getLaunchedSequence(@PathVariable Long workspaceId, @PathVariable Long launchedworkflowId, @PathVariable Long sequenceId) {
        LaunchedSequenceResponse launchedSequence = launchedSequenceService.getLaunchedSequence(sequenceId);
        return ResponseEntity.ok(launchedSequence);
    }



    @PostMapping
    public ResponseEntity<LaunchedSequenceResponse> createLaunchedSequence(@PathVariable Long workspaceId,@PathVariable Long launchedworkflowId, @RequestBody LaunchedSequenceRequest launchedSequenceRequest) {
        LaunchedSequenceResponse createdLaunchedSequence = launchedSequenceService.createLaunchedSequence(launchedSequenceRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLaunchedSequence);
    }


    @PutMapping("/{sequenceId}")
    public ResponseEntity<LaunchedSequenceResponse> updateLaunchedSequence(@PathVariable Long workspaceId,@PathVariable Long launchedworkflowId, @RequestBody @Validated(ValidationSequence.class) LaunchedSequenceRequest launchedSequenceRequest, @PathVariable Long sequenceId){
        LaunchedSequenceResponse updatedLaunchedSequence = launchedSequenceService.updateLaunchedSeqeunce(launchedSequenceRequest, sequenceId);
        return ResponseEntity.ok(updatedLaunchedSequence);
    }

    @PatchMapping("/{sequenceId}")
    public ResponseEntity<LaunchedSequenceResponse> updateLaunchedSequence(@PathVariable Long workspaceId, @PathVariable Long launchedworkflowId, @RequestBody @Validated(ValidationSequence.class) LaunchedSequenceUpdateRequest request, @PathVariable Long sequenceId){
        LaunchedSequenceResponse updatedLaunchedSequence = launchedSequenceService.updateLaunchedSequence(request, sequenceId);
        return ResponseEntity.ok(updatedLaunchedSequence);
    }

    @DeleteMapping("/{sequenceId}")
    public ResponseEntity<Void> deleteLaunchedSequence(@PathVariable Long workspaceId, @PathVariable Long launchedworkflowId, @PathVariable Long sequenceId){
        launchedSequenceService.deleteLaunchedSequence(sequenceId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
