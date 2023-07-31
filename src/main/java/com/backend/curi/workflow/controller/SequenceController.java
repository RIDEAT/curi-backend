package com.backend.curi.workflow.controller;

import com.backend.curi.exception.sequence.ValidationSequence;
import com.backend.curi.workflow.controller.dto.ModuleRequest;
import com.backend.curi.workflow.controller.dto.SequenceRequest;
import com.backend.curi.workflow.controller.dto.SequenceResponse;
import com.backend.curi.workflow.repository.SequenceModuleRepository;
import com.backend.curi.workflow.repository.entity.SequenceModule;
import com.backend.curi.workflow.service.SequenceService;
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

    @PostMapping("/sequences")
    public ResponseEntity<SequenceResponse> createSequence(@RequestBody @Validated(ValidationSequence.class) SequenceRequest request, @PathVariable Long workspaceId) {
        var createdSequence = sequenceService.createSequence(workspaceId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(SequenceResponse.of(createdSequence));
    }

    @PostMapping("workflows/{workflowId}/sequences")
    public ResponseEntity<Void> createSequence(@RequestBody @Validated(ValidationSequence.class) SequenceRequest request, @PathVariable Long workspaceId, @PathVariable Long workflowId) {
        sequenceService.createSequence(workspaceId, workflowId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/sequences/{sequenceId}")
    public ResponseEntity<SequenceResponse> getSequence(@PathVariable Long sequenceId) {
        var response = sequenceService.getSequence(sequenceId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/sequences")
    public ResponseEntity<List<SequenceResponse>> getSequences(@PathVariable Long workspaceId) {
        var response = sequenceService.getSequences(workspaceId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/sequences/{sequenceId}")
    public ResponseEntity<Void> modifySequence(@RequestBody @Validated(ValidationSequence.class) SequenceRequest request,
                                               @PathVariable Long sequenceId) {
        sequenceService.modifySequence(sequenceId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("workflows/{workflowId}/sequences/{sequenceId}")
    public ResponseEntity<Void> modifySequence(@RequestBody @Validated(ValidationSequence.class) SequenceRequest request,
                                               @PathVariable Long workflowId,
                                               @PathVariable Long sequenceId) {
        sequenceService.modifySequence(workflowId, sequenceId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/sequences/{sequenceId}")
    public ResponseEntity<Void> deleteSequence(@PathVariable Long sequenceId) {
        sequenceService.deleteSequence(sequenceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("workflows/{workflowId}/sequences/{sequenceId}")
    public ResponseEntity<Void> deleteWorkflowSequence(@PathVariable Long workflowId,
                                                       @PathVariable Long sequenceId) {
        sequenceService.deleteWorkflowSequence(workflowId, sequenceId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
