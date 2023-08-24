package com.backend.curi.workflow.controller;

import com.backend.curi.workflow.controller.dto.ContentResponse;
import com.backend.curi.workflow.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspaces/{workspaceId}/contents")
public class ContentController {
    private final ContentService contentService;

    @GetMapping("/{contentIdStr}")
    public ResponseEntity<ContentResponse> getContent(@PathVariable Long workspaceId, @PathVariable String contentIdStr){
        ObjectId contentId = new ObjectId(contentIdStr);
        var response = contentService.getContents(contentId);
        return ResponseEntity.ok(response);
    }


}
