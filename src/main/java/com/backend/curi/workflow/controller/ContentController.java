package com.backend.curi.workflow.controller;

import com.backend.curi.exception.sequence.ValidationSequence;
import com.backend.curi.workflow.controller.dto.ContentResponse;
import com.backend.curi.workflow.controller.dto.ContentUpdateRequest;
import com.backend.curi.workflow.repository.entity.contents.*;
import com.backend.curi.workflow.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspaces/{workspaceId}/workflows/{workflowId}/sequences/{sequenceId}/modules/{moduleId}/content")
public class ContentController {
    private final ModuleService moduleService;

    @GetMapping
    public ResponseEntity<ContentResponse> getContent(@PathVariable Long workspaceId, @PathVariable Long workflowId,
                                                      @PathVariable Long sequenceId, @PathVariable Long moduleId) {
        var response = moduleService.getContent(moduleId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping
    public ResponseEntity<ContentResponse> updateContent(@RequestBody @Validated(ValidationSequence.class) ContentUpdateRequest<DefaultContent> request,
                                                         @PathVariable Long workspaceId, @PathVariable Long workflowId,
                                                         @PathVariable Long sequenceId, @PathVariable Long moduleId) {
        var response = moduleService.updateContent(moduleId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/notion")
    public ResponseEntity<ContentResponse> updateNotionContent(@RequestBody @Validated(ValidationSequence.class) ContentUpdateRequest<NotionContent> request,
                                                               @PathVariable Long workspaceId, @PathVariable Long workflowId,
                                                               @PathVariable Long sequenceId, @PathVariable Long moduleId) {
        var response = moduleService.updateContent(moduleId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/web")
    public ResponseEntity<ContentResponse> updateWebContent(@RequestBody @Validated(ValidationSequence.class) ContentUpdateRequest<WebContent> request,
                                                               @PathVariable Long workspaceId, @PathVariable Long workflowId,
                                                               @PathVariable Long sequenceId, @PathVariable Long moduleId) {
        var response = moduleService.updateContent(moduleId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/google_docs")
    public ResponseEntity<ContentResponse> updateGoogleDocsContent(@RequestBody @Validated(ValidationSequence.class) ContentUpdateRequest<GoogleDocsContent> request,
                                                               @PathVariable Long workspaceId, @PathVariable Long workflowId,
                                                               @PathVariable Long sequenceId, @PathVariable Long moduleId) {
        var response = moduleService.updateContent(moduleId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/google_form")
    public ResponseEntity<ContentResponse> updateGoogleFormContent(@RequestBody @Validated(ValidationSequence.class) ContentUpdateRequest<GoogleFormContent> request,
                                                               @PathVariable Long workspaceId, @PathVariable Long workflowId,
                                                               @PathVariable Long sequenceId, @PathVariable Long moduleId) {
        var response = moduleService.updateContent(moduleId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/youtube")
    public ResponseEntity<ContentResponse> updateYoutubeContent(@RequestBody @Validated(ValidationSequence.class) ContentUpdateRequest<YoutubeContent> request,
                                                               @PathVariable Long workspaceId, @PathVariable Long workflowId,
                                                               @PathVariable Long sequenceId, @PathVariable Long moduleId) {
        var response = moduleService.updateContent(moduleId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/contents")
    public ResponseEntity<ContentResponse> updateContentsContent(@RequestBody @Validated(ValidationSequence.class) ContentUpdateRequest<ContentsContent> request,
                                                                @PathVariable Long workspaceId, @PathVariable Long workflowId,
                                                                @PathVariable Long sequenceId, @PathVariable Long moduleId) {
        var response = moduleService.updateContent(moduleId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/attachments")
    public ResponseEntity<ContentResponse> updateAttachmentsContent(@RequestBody @Validated(ValidationSequence.class) ContentUpdateRequest<AttachmentContent> request,
                                                                @PathVariable Long workspaceId, @PathVariable Long workflowId,
                                                                @PathVariable Long sequenceId, @PathVariable Long moduleId) {
        var response = moduleService.updateContent(moduleId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
