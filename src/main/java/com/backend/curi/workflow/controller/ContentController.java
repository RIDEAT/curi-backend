package com.backend.curi.workflow.controller;

import com.backend.curi.workflow.controller.dto.ContentResponse;
import com.backend.curi.workflow.controller.dto.ModuleResponse;
import com.backend.curi.workflow.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspaces/{workspaceId}/contents")
public class ContentController {
    private final ContentService contentService;

    @GetMapping("/{contentId}")
    public ResponseEntity<ContentResponse> getContent(@PathVariable Long workspaceId, @PathVariable ObjectId contentId){
        var response = contentService.getContent(contentId);
        return ResponseEntity.ok(response);
    }


}