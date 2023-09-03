package com.backend.curi.workflow.controller;

import com.backend.curi.exception.sequence.ValidationSequence;
import com.backend.curi.workflow.controller.dto.SequenceRequest;
import com.backend.curi.workflow.controller.dto.SequenceResponse;
import com.backend.curi.workflow.controller.dto.SequenceUpdateRequest;
import com.backend.curi.workflow.service.SequenceService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspaces/{workspaceId}")
public class SequenceController {
    private final SequenceService sequenceService;

    @PostMapping("workflows/{workflowId}/sequences")
    public ResponseEntity<SequenceResponse> createSequence(@RequestBody @Validated(ValidationSequence.class) SequenceRequest request, @PathVariable Long workspaceId, @PathVariable Long workflowId) {
        var createdSequence = sequenceService.createSequence(workspaceId, workflowId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(SequenceResponse.of(createdSequence));
    }

    @GetMapping("workflows/{workflowId}/sequences/{sequenceId}")
    public ResponseEntity<SequenceResponse> getSequence(@PathVariable Long sequenceId, @PathVariable Long workspaceId) {
        var response = sequenceService.getSequence(sequenceId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("workflows/{workflowId}/sequences/{sequenceId}")
    public ResponseEntity<SequenceResponse> modifySequence(@RequestBody @Validated(ValidationSequence.class) SequenceRequest request,
                                               @PathVariable Long workflowId,
                                               @PathVariable Long sequenceId) {
        var updatedSequence = sequenceService.modifySequence(workflowId, sequenceId, request);
        return ResponseEntity.status(HttpStatus.OK).body(SequenceResponse.of(updatedSequence));
    }


    @DeleteMapping("workflows/{workflowId}/sequences/{sequenceId}")
    public ResponseEntity<Void> deleteWorkflowSequence(@PathVariable Long workflowId,
                                                       @PathVariable Long sequenceId) {
        sequenceService.deleteSequence(sequenceId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("workflows/{workflowId}/sequences/{sequenceId}")
    public ResponseEntity<SequenceResponse> updateSequence(@RequestBody @Validated(ValidationSequence.class) SequenceUpdateRequest request,
                                                           @PathVariable Long workflowId,
                                                           @PathVariable Long sequenceId) {
        return ResponseEntity.status(HttpStatus.OK).body(sequenceService.updateSequence(sequenceId, request));
    }
}
